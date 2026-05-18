# API — Projeto Nora Backend

## 1. Visão geral

O backend Java do Projeto Nora é uma API REST desenvolvida com Quarkus 3 e Java 21, responsável por orquestrar o fluxo de triagem odontológica da ONG Turma do Bem. Atua como camada central entre o bot Telegram (via N8N/Gemini), o frontend React hospedado na Vercel, e os serviços Python de match geográfico publicados no Render.

A persistência é realizada no Oracle FIAP via JDBC manual. O endpoint crítico da API é `POST /triagens/{id}/aprovar`, que executa uma transação JDBC atômica cobrindo triagem, pessoa, paciente, conversa (quando presente) e encaminhamento.

---

## 2. URLs base

| Ambiente | URL |
|---|---|
| Local | `http://localhost:8080` |
| Produção | `https://<nome-do-servico>.onrender.com` |

Após o primeiro deploy bem-sucedido no Render, anotar a URL real em `docs/deploy-render.md` e configurar no frontend React via variável de ambiente.

---

## 3. Autenticação

A API usa token opaco (UUID) gerado no login e persistido na tabela `TB_COLABORADOR_SESSAO`.

### Endpoints públicos (sem autenticação)

- `POST /auth/login`
- `GET /q/health`

### Endpoints protegidos

Todos os demais. O token deve ser enviado no header:

```
Authorization: Bearer <token>
```

Ausência ou invalidade do token retorna **401**.

---

## 4. Headers padrão

| Header | Quando usar |
|---|---|
| `Content-Type: application/json` | Requests com body (POST, PUT) |
| `Authorization: Bearer <token>` | Todas as rotas protegidas |

---

## 5. Padrão de resposta

As respostas retornam a entity diretamente (sem envelope). As métricas retornam `Map` ou `List<Map>`.

### Sucesso

```json
{ "idPessoa": 1, "nomeCompleto": "João Silva", "sttsPess": "triagem" }
```

### Erro

```json
{ "erro": "Mensagem descritiva do problema." }
```

O campo é sempre `"erro"`.

---

## 6. Códigos HTTP utilizados

| Código | Situação |
|---|---|
| 200 | OK (GET, PUT, upsert de conversa existente) |
| 201 | Criado (POST bem-sucedido, aprovação bem-sucedida) |
| 400 | Payload inválido ou campo obrigatório ausente |
| 401 | Token ausente ou inválido |
| 404 | Recurso não encontrado |
| 409 | Conflito de estado (ex.: triagem já aprovada/reprovada) |
| 422 | Violação de regra de negócio (sem dentista, FKs duplas em conversa) |
| 500 | Erro interno do servidor ou serviço externo indisponível |

---

## 7. CORS

Origens aceitas:

- `http://localhost:5173` (desenvolvimento)
- `https://projeto-nora.vercel.app` (produção)

Métodos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`  
Headers: `Content-Type`, `Authorization`

---

## 8. Endpoints

### 8.1 Health

#### `GET /q/health`

Verificação de saúde da aplicação. Não exige autenticação.

**Resposta 200:**
```json
{ "status": "UP", "checks": [] }
```

---

### 8.2 Autenticação

#### `POST /auth/login`

Autentica um colaborador e retorna o token de sessão. Não exige autenticação.

**Body:**
```json
{ "email": "colaborador@nora.com", "senha": "senha123" }
```

**Resposta 200:**
```json
{ "token": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", "nome": "Diego", "perfil": "admin" }
```

**Erros:**
- 400 — email ou senha ausentes
- 401 — credenciais inválidas

---

### 8.3 Pessoas

#### `GET /pessoas`

Retorna todas as pessoas cadastradas.

**Resposta 200:** array de `Pessoa`.

```json
[
  {
    "idPessoa": 1,
    "idEndereco": 10,
    "nomeCompleto": "João Silva",
    "cpf": "123.456.789-00",
    "dataNascimento": "2010-03-15",
    "idade": 14,
    "sexo": "M",
    "email": "joao@email.com",
    "telefone": "11999999999",
    "tgChatId": "123456789",
    "canalOrig": "telegram",
    "sttsPess": "triagem",
    "dataCriacao": "2025-01-10T09:00:00",
    "dataAtualizacao": "2025-01-10T09:00:00"
  }
]
```

#### `GET /pessoas/{id}`

Retorna uma pessoa pelo ID.

**Resposta 200:** objeto `Pessoa`.  
**Resposta 404:** pessoa não encontrada.

#### `POST /pessoas`

Cadastra uma nova pessoa.

**Body mínimo:**
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

**Resposta 201:** pessoa criada com ID gerado.  
**Erros:** 400 (campos obrigatórios ausentes).

#### `PUT /pessoas/{id}`

Atualiza dados de uma pessoa existente.

**Body:** mesmos campos do POST.  
**Resposta 200:** pessoa atualizada.  
**Resposta 404:** pessoa não encontrada.

---

### 8.4 Triagens

#### `GET /triagens`

Retorna todas as triagens.

**Resposta 200:** array de `Triagem`.

#### `POST /triagens`

Cria uma triagem. `elegTriag` e `priorTriag` são calculados pelo backend (regras do domínio) — valores enviados no payload são ignorados.

**Regras de cálculo:**
- `elegTriag`: `"elegivel"` se `11 ≤ idade ≤ 17`; `"inelegivel"` fora da faixa
- `priorTriag`: `"urgente"` (nivelUrgIa ≥ 4.0) / `"alta"` (≥ 3.0) / `"media"` (≥ 2.0) / `"baixa"` (restante)

**Body:**
```json
{
  "idPessoa": 1,
  "problemaBucal": "Dor de dente persistente ha 2 semanas",
  "rendaFamiliar": "ate_1sm",
  "nivelUrgIa": 3.8,
  "confIa": 0.92
}
```

**Resposta 201:** triagem criada com `elegTriag` e `priorTriag` calculados.  
**Erros:** 400 (campos obrigatórios ausentes), 422 (regra de negócio).

#### `PUT /triagens/{id}`

Atualiza dados de uma triagem.

**Resposta 200:** triagem atualizada.  
**Resposta 404:** triagem não encontrada.

#### `POST /triagens/{id}/aprovar`

**Endpoint principal.** Executa aprovação transacional atômica — sem body.

Passos executados em uma única transação JDBC:
1. Valida existência da triagem (404 se não existe)
2. Valida se pode ser aprovada (409 se já aprovada/reprovada)
3. Consulta API Python para sugestão de dentista por match geográfico
4. Valida dentista retornado (422 se sem dentista disponível)
5. Atualiza triagem: `stts_triag = aprovada`
6. Atualiza pessoa: `stts_pess = aprovada`
7. Insere paciente com `dt_aprov = SYSDATE`
8. Se existir conversa vinculada à pessoa: migra para `acomp_paciente`
9. Insere encaminhamento com `match_auto = S`, `prev_follow = hoje + 15 dias`
10. Commit — ou rollback total se qualquer passo falhar

**Resposta 201:** encaminhamento criado.
```json
{
  "idEncaminhamento": 5,
  "idPaciente": 3,
  "idDentista": 7,
  "idTriagem": 12,
  "matchAuto": "S",
  "distKm": 4.2,
  "prioridade": "urgente",
  "metodoCalculo": "nominatim_haversine",
  "sttsEncam": "ativo",
  "prevFollow": "2025-01-25T09:00:00",
  "observacao": null
}
```

**Erros:**

| Código | Situação |
|---|---|
| 404 | Triagem não encontrada |
| 409 | Triagem já foi aprovada ou reprovada |
| 422 | Nenhum dentista disponível para o encaminhamento |
| 500 | Falha de infraestrutura ou API Python indisponível |

---

### 8.5 Conversas e Mensagens

#### `GET /conversas`

Retorna conversas. Aceita filtros opcionais:

- `?contexto=cadastro` — valores: `cadastro`, `acomp_paciente`, `acomp_dentista`
- `?stts=ativa` — valores: `ativa`, `encerrada`

**Resposta 200:** array de `Conversa`.

```json
[
  {
    "idConversa": 1,
    "canalConv": "telegram",
    "contexto": "cadastro",
    "tgThreadId": null,
    "idPessoa": 5,
    "idPaciente": null,
    "idDentista": null,
    "sttsConv": "ativa",
    "naoLidas": 2,
    "dataCriacao": "2025-01-10T09:00:00",
    "dataAtualizacao": "2025-01-10T09:00:00"
  }
]
```

**Regra de exclusividade:** apenas uma das FKs (`idPessoa`, `idPaciente`, `idDentista`) pode estar preenchida por conversa. Constraint `CHK_CONV_CTX_FK` no Oracle garante isso.

#### `GET /conversas/{id}`

Retorna uma conversa pelo ID.

**Resposta 200:** objeto `Conversa`.  
**Resposta 404:** conversa não encontrada.

#### `POST /conversas`

Cria uma conversa. Comportamento de **upsert**: se já existe conversa ativa para a mesma FK (`idPessoa`, `idPaciente` ou `idDentista`), retorna a existente com **200** em vez de criar nova.

**Body:**
```json
{
  "canalConv": "telegram",
  "contexto": "cadastro",
  "idPessoa": 1,
  "sttsConv": "ativa"
}
```

Enviar apenas **uma** das FKs: `idPessoa`, `idPaciente` ou `idDentista`.

**Respostas:**
- 201 — nova conversa criada
- 200 — conversa ativa já existia para a FK (upsert)

**Erros:**
- 404 — FK referenciada não encontrada
- 422 — mais de uma FK preenchida, ou nenhuma FK enviada

#### `GET /conversas/{id}/mensagens`

Retorna mensagens de uma conversa. Retorna array vazio se não houver mensagens (não valida existência da conversa).

**Resposta 200:** array de `Mensagem`.

```json
[
  {
    "idMensagem": 1,
    "idConversa": 3,
    "enviadoPor": "bot",
    "direcao": "entrada",
    "conteudo": "Olá, preciso de ajuda odontológica.",
    "tipoMensagem": "texto",
    "dataEnvio": "2025-01-10T09:05:00",
    "lida": "N"
  }
]
```

#### `POST /mensagens`

Cria uma mensagem em uma conversa existente.

**Body:**
```json
{
  "idConversa": 3,
  "enviadoPor": "bot",
  "direcao": "entrada",
  "conteudo": "Olá, preciso de ajuda odontológica.",
  "tipoMensagem": "texto"
}
```

**Resposta 201:** mensagem criada.  
**Erros:**
- 404 — conversa não encontrada
- 422 — `idConversa` ausente ou `conteudo` vazio

---

### 8.6 Acompanhamento

#### `POST /acomp_evento`

Registra um evento de acompanhamento vinculado a um encaminhamento.

**Body:**
```json
{
  "idEncaminhamento": 5,
  "tipoEvento": "follow_up",
  "dsEvento": "Retorno de consulta agendado.",
  "origem": "atendente",
  "tipoMensagem": null,
  "resumoIa": null
}
```

**Valores válidos para `tipoEvento`:** `follow_up`, `retorno_dentista`, `observacao`, `mudanca_status`, `sistema`

**Valores válidos para `origem`:** `n8n`, `bot`, `atendente`, `sistema`, `dentista`

**Resposta 201:** evento criado.  
**Erros:**
- 404 — encaminhamento não encontrado
- 422 — valor inválido em campo com constraint Oracle

#### `GET /encaminhamentos/{id}/eventos`

Lista eventos de acompanhamento de um encaminhamento, ordenados por data.

**Resposta 200:** array de `AcompEvento`.  
**Resposta 404:** encaminhamento não encontrado.

---

### 8.7 Métricas

Todos os endpoints de métricas exigem autenticação. Nenhum aceita parâmetros. Retornam dados consolidados sobre todos os registros no banco.

#### `GET /metricas/resumo`

Contagens totais de cada entidade principal.

**Resposta 200:**
```json
{
  "pessoas": 42,
  "triagens": 38,
  "pacientes": 25,
  "encaminhamentos": 25,
  "conversasAbertas": 18
}
```

#### `GET /metricas/triagens-por-status`

Distribuição de triagens por status.

**Resposta 200:**
```json
[
  { "status": "aprovada", "total": 25 },
  { "status": "em_analise", "total": 8 },
  { "status": "reprovada", "total": 5 }
]
```

#### `GET /metricas/encaminhamentos-por-prioridade`

Distribuição de encaminhamentos por prioridade.

**Resposta 200:**
```json
[
  { "prioridade": "alta", "total": 10 },
  { "prioridade": "urgente", "total": 8 },
  { "prioridade": "media", "total": 5 },
  { "prioridade": "baixa", "total": 2 }
]
```

#### `GET /metricas/leads-por-canal`

Contagem de pessoas por canal de origem.

**Resposta 200:**
```json
[
  { "canal": "telegram", "total": 35 },
  { "canal": "manual", "total": 7 }
]
```

#### `GET /metricas/leads-por-mes`

Contagem de pessoas cadastradas por mês.

**Resposta 200:**
```json
[
  { "anoMes": "2026-01", "total": 15 },
  { "anoMes": "2026-02", "total": 20 }
]
```

#### `GET /metricas/regioes`

Contagem de pessoas por UF, ordenado pelo total.

**Resposta 200:**
```json
[
  { "regiao": "SP", "total": 28 },
  { "regiao": "RJ", "total": 10 }
]
```

---

## 9. Fluxos principais

### 9.1 Pessoa → Triagem → Aprovação

Sequência completa para transformar um lead em paciente encaminhado:

1. `POST /pessoas` — cadastra a pessoa (lead)
2. `POST /triagens` — cria triagem vinculada à pessoa; backend calcula elegibilidade e prioridade
3. `POST /triagens/{id}/aprovar` — executa transação atômica:
   - Consulta API Python para sugerir dentista por distância
   - Atualiza triagem e pessoa
   - Cria paciente
   - Migra conversa de `cadastro` para `acomp_paciente` (se existir)
   - Cria encaminhamento com `prev_follow = hoje + 15 dias`

Após a aprovação, o frontend deve atualizar o omnichannel: o lead sai da camada de pré-triagem (`contexto = cadastro`) e aparece na camada de acompanhamento (`contexto = acomp_paciente`).

### 9.2 Aprovação de triagem sem conversa vinculada

**Regra de negócio:** uma triagem pode ser aprovada mesmo sem conversa ou mensagem vinculada.

**Contexto:** nem todo paciente vem do bot Telegram. A ONG cadastra pessoas presencialmente, e voluntários podem criar pessoa, triagem e aprovação de forma manual sem nenhuma interação via chat.

**Comportamento do `POST /triagens/{id}/aprovar`:**
- Se existir conversa vinculada à pessoa: ela é migrada para `contexto = acomp_paciente` (FK atualizada de `id_pessoa` para `id_paciente`)
- Se não existir conversa: paciente e encaminhamento são criados normalmente; um aviso é registrado no log do servidor (`[AprovacaoTriagemService] Aviso: nenhuma conversa encontrada`)
- A ausência de conversa **não bloqueia** a aprovação nem retorna erro

### 9.3 Follow-up automático

Um scheduler executa diariamente às 09:00 e processa encaminhamentos com `PREV_FOLLOW = data_atual` e `STTS_ENCAM = 'ativo'`:

1. Envia `POST` ao webhook N8N com os dados do encaminhamento
2. Registra evento em `TB_ACOMP_EVENTO` com `TIPO_EVENTO = 'follow_up'` e `ORIGEM = 'sistema'`
3. Falhas em encaminhamentos individuais são isoladas — os demais são processados normalmente

Se `N8N_WEBHOOK_FOLLOWUP_URL` não estiver configurada, o scheduler executa em modo passivo (apenas log, sem HTTP).

---

## 10. Como o frontend deve consumir esta API

### 10.1 Fluxo de login e armazenamento do token

```js
const resp = await fetch(`${baseUrl}/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, senha })
});
const { token } = await resp.json();
// Armazenar token (sessionStorage ou variável de estado)
```

### 10.2 Chamadas autenticadas

```js
const resp = await fetch(`${baseUrl}/triagens`, {
  headers: { 'Authorization': `Bearer ${token}` }
});
if (!resp.ok) {
  const { erro } = await resp.json();
  console.error(erro);
}
```

### 10.3 Tratamento de erros

- Verificar `resp.ok` antes de processar o body
- Ler o campo `erro` do body em caso de falha
- 401 → redirecionar para login
- 409 / 422 → exibir mensagem específica para o usuário
- 500 → mensagem genérica de erro de servidor

### 10.4 Mapeamento tela → endpoint

| Tela | Endpoint principal |
|---|---|
| Login | `POST /auth/login` |
| Lista de leads | `GET /pessoas` |
| Detalhe de pessoa | `GET /pessoas/{id}` |
| Triagens | `GET /triagens`, `POST /triagens` |
| Aprovação de triagem | `POST /triagens/{id}/aprovar` |
| Omnichannel / conversas | `GET /conversas?contexto=cadastro` |
| Chat de conversa | `GET /conversas/{id}/mensagens`, `POST /mensagens` |
| Dashboard | `GET /metricas/resumo` + demais `/metricas/*` |
| Eventos de acompanhamento | `GET /encaminhamentos/{id}/eventos` |

### 10.5 CORS e produção

O frontend deve rodar em `http://localhost:5173` (desenvolvimento) ou `https://projeto-nora.vercel.app` (produção). Outras origens recebem bloqueio CORS.

---

## 11. Integração externa

### 11.1 API Python — Match geográfico

URL: configurada via `API_PYTHON_BASE_URL` (padrão: `https://api-triagens.onrender.com`).

Endpoint utilizado:
```
GET /api/triagens/{idTriagem}/sugestao-dentista
```

Resposta em envelope: `{ "status": true, "code": 200, "message": "...", "data": { ... }, "erro": [] }`

O campo `data.metodo_calculo` define o comportamento:
- `nominatim_haversine` — distância real calculada; usar `distancia_km`
- `cep_fallback` — distância não calculada; `distancia_km` é null
- `sem_dentista_disponivel` — aprovação abortada com 422

A chamada Python ocorre **antes** dos INSERTs da transação para evitar problema de visibilidade transacional (o Python consulta Oracle em conexão separada e não enxerga dados não commitados).

Se a API Python estiver indisponível (timeout ou 5xx), a transação de aprovação é abortada com 500.

### 11.2 N8N — Follow-up

URL: configurada via `N8N_WEBHOOK_FOLLOWUP_URL`.

O backend envia `POST` com payload JSON contendo dados do encaminhamento. Se a variável não estiver configurada, nenhuma requisição HTTP é realizada.

---

## 12. Variáveis de ambiente

| Variável | Obrigatória | Função |
|---|---|---|
| `ORACLE_USER` | Sim | Usuário Oracle FIAP |
| `ORACLE_PASS` | Sim | Senha Oracle FIAP |
| `ORACLE_URL` | Sim | JDBC URL Oracle (ex: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`) |
| `API_PYTHON_BASE_URL` | Não | URL da API Python; se vazia, MLService cai em stub com fallback |
| `N8N_WEBHOOK_FOLLOWUP_URL` | Não | URL do webhook N8N; se vazia, scheduler em modo passivo |

Configurar no painel Render → Environment Variables. **Nunca commitar valores reais.**

---

## 13. Limitações conhecidas

- Senha de colaborador comparada em texto puro (sem hash) — limitação acadêmica consciente
- `MLService` é stub: predição de urgência IA não disponível na API Python atual; backend usa `nivelUrgIa` enviado no payload como fallback
- `paciente.stts_trat` não é atualizado no método `encerrarEncaminhamento()` — o método existe na entity mas não é chamado no fluxo atual
- `GET /conversas/{id}/mensagens` não retorna 404 se a conversa não existir — retorna array vazio
- Sem paginação nas listagens (todas retornam registros completos)
- Sem caching nos endpoints de métricas (cada request executa queries agregadas no Oracle)
- Follow-up automático sem retry formal em caso de falha no webhook

---

## 14. Documentação relacionada

- `docs/testes-manuais.md` — roteiro de testes ponta a ponta com Insomnia/Postman
- `docs/deploy-render.md` — checklist de deploy no Render
- `docs/database/setup_oracle.sql` — DDL completo das tabelas
- `docs/api-collection/nora-backend.json` — coleção Postman v2.1
