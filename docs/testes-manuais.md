# Roteiro de testes manuais — Projeto Nora Java

---

## Configuração do Postman

### 1. Importar a coleção

Importar `docs/api-collection/nora-backend.json` no Postman (File → Import).

A coleção já inclui:
- Variável `baseUrl` = `http://localhost:10000`
- Variável `token` (vazia — preenchida automaticamente pelo script abaixo)
- Variáveis `devEmail` e `devSenha` (vazias — preencher localmente)
- Todas as requisições organizadas por recurso

### 2. Preencher credenciais localmente

Após importar, clicar em **Nora Backend Java → Variables** e preencher:

| Variável | Current Value | Observação |
|---|---|---|
| `baseUrl` | `http://localhost:10000` | Já preenchida |
| `devEmail` | *(email do colaborador)* | **Não versionar** |
| `devSenha` | *(senha do colaborador)* | **Não versionar** |
| `token` | *(deixar vazio)* | Preenchido automaticamente |

> Para produção, alterar `baseUrl` para a URL do Render.

### 3. Script de token automático (já incluído na coleção)

O request `POST /auth/login` já contém o seguinte script na aba **Tests**, que salva o token automaticamente na variável da coleção após cada login bem-sucedido:

```javascript
const resp = pm.response.json();
if (resp && resp.token) {
    pm.collectionVariables.set("token", resp.token);
    console.log("Token salvo automaticamente.");
}
pm.test("Login retorna 200", function () {
    pm.response.to.have.status(200);
});
pm.test("Resposta contém token", function () {
    pm.expect(resp).to.have.property("token");
});
```

### 4. Fluxo de uso

1. Executar `POST /auth/login` — o token é salvo automaticamente em `{{token}}`.
2. Executar qualquer rota protegida — o header `Authorization: Bearer {{token}}` é aplicado automaticamente.
3. Quando o token expirar (8 horas), repetir o passo 1.

### 5. Pre-request script opcional (auto-login quando token vazio)

Para fazer login automaticamente quando a variável `token` estiver vazia, colar o script abaixo em **Nora Backend Java → Pre-request Script** (nível da collection):

```javascript
const token = pm.collectionVariables.get("token");
if (!token) {
    const email = pm.collectionVariables.get("devEmail");
    const senha = pm.collectionVariables.get("devSenha");
    const baseUrl = pm.collectionVariables.get("baseUrl");
    pm.sendRequest({
        url: baseUrl + "/auth/login",
        method: "POST",
        header: { "Content-Type": "application/json" },
        body: { mode: "raw", raw: JSON.stringify({ email, senha }) }
    }, function (err, res) {
        if (!err && res.code === 200) {
            const data = res.json();
            if (data && data.token) {
                pm.collectionVariables.set("token", data.token);
            }
        }
    });
}
```

> Este script é **opcional** — útil para sessões longas de teste sem precisar executar o login manualmente.

---

## Pré-condições

- Aplicação rodando em `http://localhost:10000` (ou URL do Render em produção)
- Oracle FIAP acessível com env vars configuradas
- `API_PYTHON_BASE_URL` configurada (ou vazia para testar fallback)
- Coleção `docs/api-collection/nora-backend.json` importada no Postman (ver "Configuração do Postman" acima)
- Variáveis `devEmail` e `devSenha` preenchidas localmente na coleção
- Pelo menos 1 colaborador cadastrado em TB_COLABORADOR
- Pelo menos 1 endereço cadastrado em TB_ENDERECO (para criar pessoas)
- Pelo menos 1 dentista disponível no banco (para o match na aprovação)

---

## Fluxo 0 — Smoke test inicial

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /q/health | 200 `{"status":"UP"}` |
| 2 | POST /auth/login `{"email":"{{devEmail}}", "senha":"{{devSenha}}"}` | 200 com `{"token":"<uuid>", "nome":"...", "perfil":"..."}` |
| 3 | Variável `token` da coleção é salva automaticamente pelo script de Tests | — |
| 4 | GET /pessoas sem header Authorization | 401 |

---

## Fluxo 1 — Pessoas

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /pessoas | 200 com lista (pode ser vazia) |
| 2 | POST /pessoas com payload válido | 201 com pessoa criada |
| 3 | GET /pessoas/{id} com ID criado | 200 com dados |
| 4 | PUT /pessoas/{id} com nome alterado | 200 com dados atualizados |
| 5 | GET /pessoas/9999 | 404 com `{"erro":"..."}` |

**Payload POST /pessoas exemplo:**
```json
{
  "idEndereco": 1,
  "nome": "Maria Teste",
  "dataNascimento": "15/06/2012",
  "telefone": "11999990000",
  "canalOrigem": "telegram"
}
```

---

## Fluxo 2 — Triagens

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /triagens | 200 com lista |
| 2 | POST /triagens com payload válido | 201 com triagem criada (elegibilidade e prioridade calculadas) |
| 3 | PUT /triagens/{id} com observação alterada | 200 |
| 4 | POST /triagens com idPessoa inexistente | 422/400 com erro |

**Payload POST /triagens exemplo:**
```json
{
  "idPessoa": 1,
  "problemaBucal": "Dor de dente persistente ha 2 semanas",
  "rendaFamiliar": "ate_1sm"
}
```

---

## Fluxo 3 — Aprovação transacional (fluxo crítico)

> Pré-condição: triagem existente com stts_triag = 'em_analise' e ao menos 1 dentista ativo com vagas no banco.

| Passo | Request | Esperado |
|---|---|---|
| 1 | POST /triagens/{id}/aprovar | 201 com encaminhamento criado |
| 2 | Verificar banco: TB_TRIAGEM → stts_triag='aprovada' | SELECT |
| 3 | Verificar banco: TB_PESSOA → stts_pess='aprovada' | SELECT |
| 4 | Verificar banco: TB_PACIENTE → novo registro | SELECT |
| 5 | Verificar banco: TB_CONVERSA → contexto='acomp_paciente', fk_pess_id=null | SELECT |
| 6 | Verificar banco: TB_ENCAMINHAMENTO → novo registro com match_auto='S' | SELECT |
| 7 | POST /triagens/{id}/aprovar novamente (mesma triagem) | 409 "JA_PROCESSADA" |
| 8 | POST /triagens/9999/aprovar | 404 "NAO_ENCONTRADA" |

### Fluxo 3b — Aprovação sem conversa vinculada

> Cenário: pessoa cadastrada presencialmente pela ONG, sem interação via bot ou chat.
> Pré-condição: pessoa e triagem criadas, **sem** registro em TB_CONVERSA para essa pessoa.

| Passo | Request | Esperado |
|---|---|---|
| 1 | POST /triagens/{id}/aprovar | 201 com encaminhamento criado |
| 2 | Verificar banco: TB_PACIENTE → novo registro criado | SELECT |
| 3 | Verificar banco: TB_ENCAMINHAMENTO → novo registro com match_auto='S' | SELECT |
| 4 | Verificar banco: TB_CONVERSA → nenhuma linha para esse idPessoa | SELECT |
| 5 | Log do servidor | Deve conter `Aviso: nenhuma conversa encontrada` — não é erro |

**Comportamento esperado:** aprovação bem-sucedida (201). A ausência de conversa não bloqueia a transação.

---

## Fluxo 4 — Omnichannel (conversas e mensagens)

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /conversas | 200 com lista |
| 2 | POST /conversas com idPessoa válido | 201 com conversa criada |
| 3 | POST /conversas com mesmo idPessoa (upsert) | 200 com conversa existente |
| 4 | POST /conversas com idPessoa + idPaciente preenchidos | 422 "REGRA" |
| 5 | POST /conversas sem nenhuma FK | 422 "REGRA" |
| 6 | GET /conversas/{id} | 200 |
| 7 | GET /conversas/{id}/mensagens | 200 com lista (pode ser vazia) |
| 8 | POST /mensagens com idConversa válido | 201 com mensagem criada |
| 9 | POST /mensagens com idConversa inexistente | 404 "NAO_ENCONTRADA" |
| 10 | POST /mensagens sem conteudo | 422 "REGRA" |

**Payload POST /conversas exemplo:**
```json
{
  "canalConv": "telegram",
  "contexto": "cadastro",
  "idPessoa": 1
}
```

**Payload POST /mensagens exemplo:**
```json
{
  "idConversa": 1,
  "enviadoPor": "usuario",
  "direcao": "entrada",
  "conteudo": "Ola, preciso de ajuda odontologica.",
  "tipoMensagem": "texto"
}
```

> Valores válidos para `enviadoPor`: `usuario`, `nora_ia`, `externo`.

---

## Fluxo 5 — Acompanhamento

| Passo | Request | Esperado |
|---|---|---|
| 1 | POST /acomp_evento com payload válido | 201 com evento criado |
| 2 | GET /encaminhamentos/{id}/eventos | 200 com lista ordenada por data |
| 3 | POST /acomp_evento com idEncaminhamento inexistente | 404 "NAO_ENCONTRADA" |
| 4 | POST /acomp_evento com tipoEvento inválido | 422 "REGRA" |
| 5 | GET /encaminhamentos/9999/eventos | 404 "NAO_ENCONTRADA" |

**Payload POST /acomp_evento exemplo:**
```json
{
  "idEncaminhamento": 1,
  "tipoEvento": "atualizacao",
  "dsEvento": "Retorno de consulta agendado com dentista.",
  "origem": "atendente"
}
```

> Valores válidos para `tipoEvento`: `primeira_consulta`, `atualizacao`, `abandono`, `conclusao`, `reencaminhamento`, `followup`, `outro`.  
> Valores válidos para `origem`: `paciente`, `dentista`, `ia`, `atendente`, `sistema`.

---

## Fluxo 6 — Métricas

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /metricas/resumo | 200 com objeto plano `{totalLeads, totalAprovados, totalEncaminhamentos, totalDentistasAtivos, taxaAprovacao, taxaConclusao}` |
| 2 | GET /metricas/triagens-por-status | 200 com objeto plano — chaves `em_analise`, `aprovada`, `encerrada`, `inativa` (sempre presentes) |
| 3 | GET /metricas/encaminhamentos-por-prioridade | 200 com objeto plano — chaves `baixa`, `media`, `alta`, `urgente` (sempre presentes) |
| 4 | GET /metricas/leads-por-canal | 200 com objeto plano — chaves `telegram`, `whatsapp`, `instagram`, `facebook`, `outro` (sempre presentes) |
| 5 | GET /metricas/leads-por-mes | 200 com objeto plano — chaves `Jan` a `Dez` (sempre presentes, zero se sem dados) |
| 6 | GET /metricas/regioes | 200 com objeto plano — chaves = nomes de bairros, agrupado por bairro |

> Todos os endpoints de métricas retornam objeto JSON plano. Chaves com valor zero são incluídas (nunca ausentes).

---

## Fluxo 7 — Follow-up manual

> Pré-condição: existir ao menos um encaminhamento com `STTS_ENCAM = 'ativo'` e `PREV_FOLLOW <= hoje`.

| Passo | Request | Esperado |
|---|---|---|
| 1 | POST /follow-up/executar | 200 `{"mensagem":"Follow-up executado."}` |
| 2 | Verificar logs do servidor | Deve conter `[FollowUpService] Encaminhamentos elegíveis: N` |
| 3 | Se `N8N_WEBHOOK_FOLLOWUP_URL` configurada: verificar N8N recebeu payload | Payload com encaminhamentoId, pacienteNome, telefonePaciente, etc. |
| 4 | Verificar banco: TB_ACOMP_EVENTO | Novo evento com `TP_EVENTO = 'followup'` e `ORIGEM = 'sistema'` |
| 5 | POST /follow-up/executar novamente (mesmo encaminhamento ativo) | 200 — segue processando (sem idempotência: dispara novamente) |

> **Limitação:** `PREV_FOLLOW` não é atualizado após o disparo. O encaminhamento continua elegível nas próximas execuções enquanto `STTS_ENCAM = 'ativo'`. O N8N deve tratar duplicidade, ou o colaborador deve atualizar o status do encaminhamento.

---

## Cenários de erro esperados

| Cenário | Request | HTTP esperado |
|---|---|---|
| Sem token | Qualquer rota protegida | 401 |
| Token inválido | Authorization: Bearer invalido | 401 |
| Triagem não encontrada | POST /triagens/9999/aprovar | 404 |
| Triagem já aprovada | POST /triagens/{id}/aprovar 2x | 409 |
| Sem dentista disponível | POST /triagens/{id}/aprovar sem dentista | 422 |
| Conversa com 2 FKs | POST /conversas com idPessoa + idPaciente | 422 |
| Mensagem sem conversa | POST /mensagens com idConversa=9999 | 404 |
| Evento com tipo inválido | POST /acomp_evento tipoEvento="inexistente" | 422 |
| Sem dentista disponível no banco | POST /triagens/{id}/aprovar | 422 |

---

## Checklist de evidências para banca

- [ ] Captura: POST /auth/login bem-sucedido (200 com token)
- [ ] Captura: POST /triagens bem-sucedido (201 com elegibilidade e prioridade calculadas)
- [ ] Captura: POST /triagens/{id}/aprovar bem-sucedido (201 com encaminhamento)
- [ ] Captura: POST /triagens/{id}/aprovar duplicado (409)
- [ ] Captura: GET /metricas/resumo (200 com chaves totalLeads, taxaAprovacao, etc.)
- [ ] Captura: GET /metricas/triagens-por-status (200 com objeto plano)
- [ ] Captura: POST /acomp_evento (201)
- [ ] Captura: POST /follow-up/executar (200 com mensagem de confirmação)
- [ ] SELECTs no Oracle antes e depois da aprovação (TB_TRIAGEM, TB_PACIENTE, TB_CONVERSA, TB_ENCAMINHAMENTO)
- [ ] Logs do console mostrando `[AprovacaoTriagemService]` e chamada ao MatchService
- [ ] GET /q/health em produção (Render)
