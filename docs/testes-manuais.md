# Roteiro de testes manuais — Projeto Nora Java

## Pré-condições

- Aplicação rodando em `http://localhost:8080` (ou URL do Render em produção)
- Oracle FIAP acessível com env vars configuradas
- `API_PYTHON_BASE_URL` configurada (ou vazia para testar fallback)
- Coleção `docs/api-collection/nora-backend.json` importada no Insomnia/Postman
- Variável `baseUrl` = `http://localhost:8080` configurada na coleção
- Pelo menos 1 colaborador cadastrado em TB_COLABORADOR
- Pelo menos 1 endereço cadastrado em TB_ENDERECO (para criar pessoas)
- Pelo menos 1 dentista disponível no banco (para o match na aprovação)

---

## Fluxo 0 — Smoke test inicial

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /q/health | 200 `{"status":"UP"}` |
| 2 | POST /auth/login `{"email":"...", "senha":"..."}` | 200 com `{"token":"<uuid>"}` |
| 3 | Salvar token na variável `token` da coleção | — |
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
  "nomeCompleto": "Maria Teste",
  "dataNascimento": "2012-06-15",
  "idade": 13,
  "telefone": "11999990000",
  "canalOrig": "telegram"
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

> Pré-condição: triagem existente com stts_triag = 'em_analise' e API Python disponível.

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
  "enviadoPor": "bot",
  "direcao": "entrada",
  "conteudo": "Ola, preciso de ajuda odontologica.",
  "tipoMensagem": "texto"
}
```

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
  "tipoEvento": "follow_up",
  "dsEvento": "Retorno de consulta agendado com dentista.",
  "origem": "atendente"
}
```

---

## Fluxo 6 — Métricas

| Passo | Request | Esperado |
|---|---|---|
| 1 | GET /metricas/resumo | 200 com objeto `{pessoas, triagens, pacientes, encaminhamentos, conversasAbertas}` |
| 2 | GET /metricas/triagens-por-status | 200 com lista `[{status, total}]` |
| 3 | GET /metricas/encaminhamentos-por-prioridade | 200 com lista `[{prioridade, total}]` |
| 4 | GET /metricas/leads-por-canal | 200 com lista `[{canal, total}]` |
| 5 | GET /metricas/leads-por-mes | 200 com lista `[{anoMes, total}]` ex: `"anoMes":"2026-01"` |
| 6 | GET /metricas/regioes | 200 com lista `[{regiao, total}]` agrupado por UF |

> Todos os endpoints de métricas retornam lista vazia se não houver dados — nunca 404.

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
| API Python off | POST /triagens/{id}/aprovar | 500 controlado |

---

## Checklist de evidências para banca

- [ ] Captura: POST /auth/login bem-sucedido (200 com token)
- [ ] Captura: POST /triagens bem-sucedido (201 com elegibilidade e prioridade calculadas)
- [ ] Captura: POST /triagens/{id}/aprovar bem-sucedido (201 com encaminhamento)
- [ ] Captura: POST /triagens/{id}/aprovar duplicado (409)
- [ ] Captura: GET /metricas/resumo (200 com contagens reais)
- [ ] Captura: GET /metricas/triagens-por-status (200)
- [ ] Captura: POST /acomp_evento (201)
- [ ] SELECTs no Oracle antes e depois da aprovação (TB_TRIAGEM, TB_PACIENTE, TB_CONVERSA, TB_ENCAMINHAMENTO)
- [ ] Logs do console mostrando `[AprovacaoTriagemService]` e chamada ao MatchService
- [ ] GET /q/health em produção (Render)
