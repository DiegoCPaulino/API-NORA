# API — Projeto Nora Backend

## 1. Visão geral

O backend Java do Projeto Nora é uma API REST desenvolvida com Quarkus 3 e Java 21, responsável por orquestrar o fluxo de triagem odontológica da ONG Turma do Bem. Atua como camada central entre o bot Telegram (via N8N/Gemini), o frontend React hospedado na Vercel e o banco Oracle FIAP.

A persistência é realizada no Oracle FIAP via JDBC manual. O endpoint crítico da API é `POST /triagens/{id}/aprovar`, que executa uma transação JDBC atômica cobrindo triagem, pessoa, paciente, conversa (quando presente) e encaminhamento.

---

## 2. URLs base

| Ambiente | URL |
|---|---|
| Local | `http://localhost:10000` |
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

As respostas retornam DTOs ou entities diretamente (sem envelope). As métricas retornam objetos JSON planos (`Map<String, Number>`) — chaves sempre presentes, zero onde não há dados.

### Sucesso

```json
{ "id": 1, "nome": "João Silva", "status": "triagem" }
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

> Alternativamente, use o campo `login` no lugar de `email` (mesmo comportamento): `{ "login": "colaborador@nora.com", "senha": "senha123" }`.

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

Retorna todas as pessoas cadastradas. Resposta no formato `PacienteResponseDTO` (mesmo DTO de `/pacientes`).

**Resposta 200:** array de `PacienteResponseDTO`.

```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "telefone": "11999999999",
    "email": "joao@email.com",
    "dataNascimento": "15/03/2010",
    "idade": 15,
    "sexo": "M",
    "cep": "01310-100",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "uf": "SP",
    "canalOrigem": "telegram",
    "dataCadastro": "10/01/2025",
    "status": "triagem",
    "problemaBucal": "Dor de dente persistente",
    "rendaFamiliar": "ate_1sm",
    "nivelUrgenciaIA": 3.8,
    "confIA": 0.92,
    "triagens": [],
    "encaminhamentos": []
  }
]
```

> **Formato de datas:** todas as datas de resposta usam `dd/MM/yyyy` (ex.: `"10/01/2025"`). Não use formato ISO nas chamadas — o campo `dataNascimento` no request também segue `dd/MM/yyyy`.
>
> **Campos ausentes vs. request:** o response inclui `id`, `idade`, `status`, `dataCadastro`, `triagens[]`, `encaminhamentos[]` que não existem no request. O request inclui `cpf`, `rg`, `email`, `tgChatId` que podem não aparecer populados se não informados.

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
  "nome": "Maria Teste",
  "dataNascimento": "15/06/2012",
  "telefone": "11999990000",
  "canalOrigem": "telegram"
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

**Resposta 200:** array de `TriagemResponseDTO`.
```json
[
  {
    "id": 1,
    "elegibilidade": "elegivel",
    "prioridade": "alta",
    "sexo": "M",
    "problemaBucal": "Dor de dente persistente ha 2 semanas",
    "rendaFamiliar": "ate_1sm",
    "nivelUrgenciaIA": 3.8,
    "confiancaIA": 0.92,
    "sugestaoIA": null,
    "observacao": null,
    "dataTriagem": "10/01/2025",
    "status": "em_analise",
    "decisao": null,
    "pessoaId": 5
  }
]
```

> **Divergência request → response:** o campo enviado no POST como `nivelUrgIa` é retornado como `nivelUrgenciaIA`; `confIa` → `confiancaIA`; `observacoes` → `observacao`. `nivelUrgenciaIA` e `confiancaIA` podem ser `null` se o MLService não estiver configurado.

#### `GET /triagens/{id}`

Busca uma triagem pelo ID.

**Resposta 200:** objeto `TriagemResponseDTO`.  
**Resposta 404:** triagem não encontrada.

#### `POST /triagens`

Cria uma triagem. `elegTriag` e `priorTriag` são calculados pelo backend (regras do domínio) — valores enviados no payload são ignorados.

**Regras de cálculo:**
- `elegTriag`: `"elegivel"` se `11 ≤ idade ≤ 17`; `"inelegivel"` fora da faixa
- `priorTriag`: `"urgente"` (nivelUrgIa ≥ 4.0) / `"alta"` (≥ 3.0) / `"media"` (≥ 2.0) / `"baixa"` (restante)

**Body:**
```json
{
  "pessoaId": 1,
  "problemaBucal": "Dor de dente persistente ha 2 semanas",
  "rendaFamiliar": "ate_1sm",
  "nivelUrgIa": 3.8,
  "confIa": 0.92
}
```

> O campo da pessoa aceita `pessoaId` (nome primário) ou `idPessoa` (alias). `rendaFamiliar` aceita: `ate_1sm`, `1_3sm`, `acima_3sm`. `observacoes` (alias: `obsTriag`) é opcional.

**Resposta 201:** `TriagemResponseDTO`. `elegibilidade` e `prioridade` são calculados pelo backend — valores do payload são ignorados. Se `ML_PREDICT_URL` não estiver configurada, `nivelUrgenciaIA` e `confiancaIA` serão `null` na resposta.  
**Erros:** 400 (campos obrigatórios ausentes), 422 (regra de negócio).

#### `PUT /triagens/{id}`

Atualiza status e decisão de uma triagem. **Apenas os campos `status` e `decisao` são lidos** — demais campos enviados no body são ignorados.

**Body:**
```json
{ "status": "encerrada", "decisao": "reanalise" }
```

> `status` aceita: `em_analise`, `aprovada`, `encerrada`, `inativa`. `decisao` aceita: `aprovado`, `encerrado`, `reanalise`.

**Resposta 200:** triagem atualizada.  
**Resposta 404:** triagem não encontrada.

#### `POST /triagens/{id}/aprovar`

**Endpoint principal.** Executa aprovação transacional atômica — sem body.

Passos executados em uma única transação JDBC:
1. Valida existência da triagem (404 se não existe)
2. Valida se pode ser aprovada (409 se já aprovada)
3. Consulta dentistas disponíveis no banco Oracle (match interno) — 422 se nenhum disponível
4. Atualiza triagem: `stts_triag = aprovada`
5. Atualiza pessoa: `stts_pess = aprovada`
6. Insere paciente com `dt_aprov = SYSDATE`
7. Se existir conversa vinculada à pessoa: migra para `acomp_paciente`
8. Insere encaminhamento com `match_auto = S`, `dist_km = null`, `prev_follow = hoje + 15 dias`
9. Commit — ou rollback total se qualquer passo falhar

> **Match interno:** o dentista é selecionado diretamente do banco Oracle (primeiro dentista ativo com vagas). Não há chamada a serviço externo.

**Resposta 201:** encaminhamento criado (retorna `EncaminhamentoResponseDTO`). `distancia_km` será `null` para encaminhamentos gerados automaticamente.

**Erros:**

| Código | Situação |
|---|---|
| 404 | Triagem não encontrada |
| 409 | Triagem já foi aprovada |
| 422 | Nenhum dentista disponível |
| 500 | Falha de infraestrutura |

---

### 8.5 Pacientes

#### `GET /pacientes`

Retorna todos os pacientes aprovados com dados da pessoa vinculada.

**Resposta 200:** array de `PacienteResponseDTO`.

#### `GET /pacientes/{id}`

Busca um paciente pelo **ID da pessoa** (`id_pess`). **Não é o `id_pac` da tabela `TB_PACIENTE`** — o frontend deve passar o mesmo ID que obtém de `GET /pessoas`. Retorna 404 se a pessoa não existir ou não for paciente.

**Resposta 200:** objeto `PacienteResponseDTO`.  
**Resposta 404:** paciente não encontrado.

---

### 8.6 Dentistas

#### `GET /dentistas`

Retorna todos os dentistas cadastrados.

**Resposta 200:** array de `DentistaResponseDTO`.
```json
[
  {
    "id": 1,
    "nome": "Dr. Carlos Silva",
    "cro": "SP-12345",
    "telefone": "11988880000",
    "email": "carlos@email.com",
    "cep": "01310-100",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "uf": "SP",
    "capMensal": 5,
    "encaminhamentosAtivos": 2,
    "status": "ativo",
    "dataCredenciamento": "10/01/2025",
    "especialidades": ["Ortodontia", "Clínica Geral"],
    "encaminhamentos": []
  }
]
```

> **Divergência request → response:** o request envia `especialidadeIds` (lista de IDs numéricos); o response retorna `especialidades` (lista de strings com os nomes). `encaminhamentos` é uma lista de objetos `EncaminhamentoDentistaDTO`.

#### `GET /dentistas/disponiveis`

Retorna dentistas com `stts_dent = 'ativo'` e vagas disponíveis (encaminhamentos ativos < capacidade mensal).

**Resposta 200:** array de `DentistaResponseDTO`.

#### `GET /dentistas/{id}`

Busca um dentista pelo ID.

**Resposta 200:** objeto `DentistaResponseDTO`.  
**Resposta 404:** dentista não encontrado.

#### `POST /dentistas`

Cadastra um novo dentista.

**Body:**
```json
{
  "nome": "Dr. Carlos Silva",
  "cro": "SP-12345",
  "telefone": "11988880000",
  "email": "carlos@email.com",
  "capMensal": 5,
  "status": "ativo",
  "cep": "01310-100",
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "especialidadeIds": [1, 2]
}
```

> `status` aceita: `ativo`, `inativo`. `especialidadeIds` é opcional. Campos de endereço (`cep`, `bairro`, `cidade`, `uf`, `logradouro`, `numero`, `complemento`) são opcionais.

**Resposta 201:** dentista criado.  
**Erros:** 400 (campos obrigatórios ausentes).

#### `PUT /dentistas/{id}`

Atualiza dados de um dentista existente.

**Resposta 200:** dentista atualizado.  
**Resposta 404:** dentista não encontrado.

---

### 8.7 Encaminhamentos

#### `GET /encaminhamentos`

Retorna todos os encaminhamentos.

**Resposta 200:** array de `EncaminhamentoResponseDTO`.
```json
[
  {
    "id": 1,
    "paciente": {
      "id": 3,
      "nome": "Maria Teste",
      "idade": 14,
      "bairro": "Centro",
      "problemaBucal": "Dor de dente persistente",
      "nivelUrgenciaIA": 3.8
    },
    "dentista": {
      "id": 7,
      "nome": "Dr. Carlos Silva",
      "cro": "SP-12345",
      "bairro": "Bela Vista",
      "especialidades": ["Clínica Geral"],
      "distanciaKm": null
    },
    "prioridade": "alta",
    "status": "ativo",
    "dataEncaminhamento": "10/01/2025",
    "previsaoFollowUp": "25/01/2025",
    "observacao": null,
    "matchAutomatico": true,
    "followUps": []
  }
]
```

> **Divergência request → response:** o request envia `idPaciente` e `idDentista` (FKs numéricas); o response retorna objetos aninhados `paciente{}` e `dentista{}`. O campo `matchAuto` do request (`S`/`N`) é retornado como `matchAutomatico` (boolean). `distanciaKm` é sempre `null` em encaminhamentos criados pelo fluxo automático.

#### `GET /encaminhamentos/{id}`

Busca um encaminhamento pelo ID.

**Resposta 200:** objeto `EncaminhamentoResponseDTO`.  
**Resposta 404:** encaminhamento não encontrado.

#### `POST /encaminhamentos`

Cria um encaminhamento manual.

**Resposta 201:** encaminhamento criado.  
**Erros:** 400 (campos obrigatórios ausentes), 404 (paciente ou dentista não encontrado), 422 (regra de negócio).

#### `PUT /encaminhamentos/{id}`

Atualiza `sttsEncam` e/ou `obsEncam` de um encaminhamento.

**Body:** apenas os campos `status` e `obsEncam` são lidos — demais campos são ignorados.
```json
{ "status": "concluido", "obsEncam": "Tratamento finalizado com sucesso." }
```

> `status` aceita: `ativo`, `concluido`, `cancelado`, `reencaminhado`.

**Resposta 200:** encaminhamento atualizado.  
**Resposta 404:** encaminhamento não encontrado.

---

### 8.8 Conversas e Mensagens

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
    "naoLidas": 0,
    "ultimaMensagem": "Ola, preciso de ajuda.",
    "ultimoHorario": "10/01/2025",
    "dadosPaciente": { "id": 5, "nome": "João Silva", "telefone": "11999999999" },
    "dadosDentista": null,
    "mensagens": []
  }
]
```

> `naoLidas` é sempre `0` — a coluna `lida` não existe no DDL atual. `mensagens` é vazio na listagem; populado apenas em `GET /conversas/{id}`.

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

Retorna mensagens de uma conversa, ordenadas cronologicamente. Retorna array vazio se não houver mensagens.

**Resposta 200:** array de `MensagemDTO`.

```json
[
  {
    "idMensagem": 1,
    "idConversa": 3,
    "enviadoPor": "nora_ia",
    "direcao": "saida",
    "conteudo": "Olá, preciso de ajuda odontológica.",
    "tipoMensagem": "texto",
    "dataEnvio": "10/01/2025"
  }
]
```

**Resposta 404:** conversa não encontrada.

#### `POST /mensagens`

Cria uma mensagem em uma conversa existente.

**Body:**
```json
{
  "idConversa": 3,
  "enviadoPor": "usuario",
  "direcao": "entrada",
  "conteudo": "Olá, preciso de ajuda odontológica.",
  "tipoMensagem": "texto"
}
```

**Valores válidos para `enviadoPor`:** `usuario`, `nora_ia`, `externo`  
**Valores válidos para `direcao`:** `entrada`, `saida`  
**Valores válidos para `tipoMensagem`:** `texto`, `audio`, `imagem`, `documento` (padrão: `texto`)

**Resposta 201:** mensagem criada.  
**Erros:**
- 404 — conversa não encontrada
- 422 — `idConversa` ausente ou `conteudo` vazio

---

### 8.9 Acompanhamento

#### `POST /acomp_evento`

Registra um evento de acompanhamento vinculado a um encaminhamento.

**Body:**
```json
{
  "idEncaminhamento": 5,
  "tipoEvento": "atualizacao",
  "dsEvento": "Retorno de consulta agendado.",
  "origem": "atendente"
}
```

**Valores válidos para `tipoEvento`:** `primeira_consulta`, `atualizacao`, `abandono`, `conclusao`, `reencaminhamento`, `followup`, `outro`

**Valores válidos para `origem`:** `paciente`, `dentista`, `ia`, `atendente`, `sistema`

**Resposta 201:** evento criado.  
**Erros:**
- 404 — encaminhamento não encontrado
- 422 — valor inválido em campo com constraint Oracle

#### `GET /encaminhamentos/{id}/eventos`

> ⚠️ **Pendência conhecida:** esta rota retorna 404 em runtime por defeito de mapeamento JAX-RS. **Não consumi-la no frontend por enquanto.**
>
> A lista de eventos de acompanhamento está disponível no campo `followUps` da resposta de `GET /encaminhamentos/{id}` (ver seção 8.7). Use esse endpoint como substituto.

Quando corrigida, retornará array de `FollowUpDTO`:
```json
[
  {
    "id": 1,
    "tipoEvento": "followup",
    "descricao": "Follow-up automático disparado pelo sistema.",
    "origem": "sistema",
    "dataEvento": "23/05/2026"
  }
]
```

---

### 8.10 Métricas

Todos os endpoints de métricas exigem autenticação. Nenhum aceita parâmetros. Retornam dados consolidados sobre todos os registros no banco.

#### `GET /metricas/resumo`

Totais e taxas consolidadas da operação.

**Resposta 200:**
```json
{
  "totalLeads": 42,
  "totalAprovados": 25,
  "totalEncaminhamentos": 25,
  "totalDentistasAtivos": 8,
  "taxaAprovacao": 0.595,
  "taxaConclusao": 0.32
}
```

`taxaAprovacao = totalAprovados / totalLeads`. `taxaConclusao = encaminhamentos concluídos / totalEncaminhamentos`. Ambas retornam `0.0` se o denominador for zero.

#### `GET /metricas/triagens-por-status`

Distribuição de triagens por status. Todas as 4 chaves são sempre retornadas (zero se sem dados).

**Resposta 200:**
```json
{
  "em_analise": 8,
  "aprovada": 25,
  "encerrada": 3,
  "inativa": 2
}
```

#### `GET /metricas/encaminhamentos-por-prioridade`

Distribuição de encaminhamentos por prioridade. Todas as 4 chaves são sempre retornadas.

**Resposta 200:**
```json
{
  "baixa": 2,
  "media": 5,
  "alta": 10,
  "urgente": 8
}
```

#### `GET /metricas/leads-por-canal`

Contagem de pessoas por canal de origem. Todas as 5 chaves são sempre retornadas.

**Resposta 200:**
```json
{
  "telegram": 35,
  "whatsapp": 5,
  "instagram": 2,
  "facebook": 1,
  "outro": 7
}
```

#### `GET /metricas/leads-por-mes`

Contagem de pessoas cadastradas por mês (todos os anos agregados). As 12 chaves são sempre retornadas.

**Resposta 200:**
```json
{
  "Jan": 5,
  "Fev": 8,
  "Mar": 12,
  "Abr": 7,
  "Mai": 4,
  "Jun": 0,
  "Jul": 0,
  "Ago": 3,
  "Set": 2,
  "Out": 1,
  "Nov": 0,
  "Dez": 0
}
```

#### `GET /metricas/regioes`

Contagem de pessoas por bairro, ordenado pelo total decrescente. Pessoas sem endereço aparecem como `"Nao informado"`.

**Resposta 200:**
```json
{
  "Centro": 28,
  "Pinheiros": 10,
  "Nao informado": 4
}
```

### 8.11 Follow-up manual

#### `POST /follow-up/executar`

Executa imediatamente o mesmo fluxo do scheduler diário, sem aguardar o cron das 09:00. Útil para testes e para processar encaminhamentos fora do horário agendado.

Sem body. Requer autenticação (Bearer token).

**Resposta 200:**
```json
{ "mensagem": "Follow-up executado." }
```

**Resposta 500:** erro interno durante execução.

> O endpoint aciona o mesmo `FollowUpService` do scheduler. Se `N8N_WEBHOOK_FOLLOWUP_URL` não estiver configurada, encerra normalmente com log informativo.

---

## 9. Fluxos principais

### 9.1 Pessoa → Triagem → Aprovação

Sequência completa para transformar um lead em paciente encaminhado:

1. `POST /pessoas` — cadastra a pessoa (lead)
2. `POST /triagens` — cria triagem vinculada à pessoa; backend calcula elegibilidade e prioridade
3. `POST /triagens/{id}/aprovar` — executa transação atômica:
   - Seleciona primeiro dentista disponível no banco Oracle (match interno)
   - Atualiza triagem e pessoa
   - Cria paciente
   - Migra conversa de `cadastro` para `acomp_paciente` (se existir)
   - Cria encaminhamento com `match_auto = S`, `dist_km = null`, `prev_follow = hoje + 15 dias`

Após a aprovação, o frontend deve atualizar o omnichannel: o lead sai da camada de pré-triagem (`contexto = cadastro`) e aparece na camada de acompanhamento (`contexto = acomp_paciente`).

### 9.2 Aprovação de triagem sem conversa vinculada

**Regra de negócio:** uma triagem pode ser aprovada mesmo sem conversa ou mensagem vinculada.

**Contexto:** nem todo paciente vem do bot Telegram. A ONG cadastra pessoas presencialmente, e voluntários podem criar pessoa, triagem e aprovação de forma manual sem nenhuma interação via chat.

**Comportamento do `POST /triagens/{id}/aprovar`:**
- Se existir conversa vinculada à pessoa: ela é migrada para `contexto = acomp_paciente` (FK atualizada de `id_pessoa` para `id_paciente`)
- Se não existir conversa: paciente e encaminhamento são criados normalmente; um aviso é registrado no log do servidor (`[AprovacaoTriagemService] Aviso: nenhuma conversa encontrada`)
- A ausência de conversa **não bloqueia** a aprovação nem retorna erro

### 9.3 Follow-up automático

Um scheduler executa diariamente às 09:00 e processa encaminhamentos com `PREV_FOLLOW <= data_atual` e `STTS_ENCAM = 'ativo'` (inclui follow-ups atrasados):

1. Envia `POST` ao webhook N8N com payload enriquecido: `encaminhamentoId`, `pacienteId`, `pacienteNome`, `telefonePaciente`, `canalOrigem`, `dentistaId`, `dentistaNome`, `previsaoFollowUp`, `prioridade`, `origem: "backend-java"`
2. Registra evento em `TB_ACOMP_EVENTO` com `TIPO_EVENTO = 'followup'` e `ORIGEM = 'sistema'`
3. Falhas em encaminhamentos individuais são isoladas — os demais são processados normalmente

Se `N8N_WEBHOOK_FOLLOWUP_URL` não estiver configurada, o scheduler executa em modo passivo (apenas log, sem HTTP).

**Endpoint manual:** `POST /follow-up/executar` executa o mesmo fluxo imediatamente, sem aguardar o cron.

**Limitação:** `PREV_FOLLOW` não é atualizado após o disparo. O mesmo encaminhamento é processado novamente nas execuções seguintes enquanto `STTS_ENCAM = 'ativo'`. O N8N deve ser configurado para tratar duplicidade, ou o colaborador deve atualizar o status do encaminhamento.

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
| Eventos de acompanhamento | `GET /encaminhamentos/{id}` (campo `followUps`) — rota `/eventos` inoperante |

### 10.5 CORS e produção

O frontend deve rodar em `http://localhost:5173` (desenvolvimento) ou `https://projeto-nora.vercel.app` (produção). Outras origens recebem bloqueio CORS.

---

## 11. Integrações externas

### 11.1 Match de dentista — interno ao banco Oracle

O match de dentista no fluxo de aprovação é realizado **internamente**: o `MatchService` consulta a tabela `dentista` no Oracle e seleciona o primeiro dentista com `stts_dent = 'ativo'` e vagas disponíveis (encaminhamentos ativos < `cap_mensal`). Não há chamada a serviço externo. `dist_km` é sempre `null` em encaminhamentos criados pelo fluxo automático.

Se nenhum dentista estiver disponível, a aprovação é abortada com **422**.

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
| `N8N_WEBHOOK_FOLLOWUP_URL` | Não | URL do webhook N8N; se vazia, scheduler em modo passivo |
| `ML_PREDICT_URL` | Não | URL do endpoint de predição IA; se vazia, usa `nivelUrgIa` do payload como fallback |

Configurar no painel Render → Environment Variables. **Nunca commitar valores reais.**

---

## 13. Limitações conhecidas

- `GET /encaminhamentos/{id}/eventos` retorna 404 em runtime por defeito de mapeamento JAX-RS — usar o campo `followUps` de `GET /encaminhamentos/{id}` como substituto
- Senha de colaborador comparada em texto puro (sem hash) — limitação acadêmica consciente
- `MLService` é stub: se `ML_PREDICT_URL` não estiver configurada, backend usa `nivelUrgIa` enviado no payload como fallback (retorna `null` silenciosamente)
- Match de dentista é interno ao banco Oracle (`dist_km` sempre `null` em encaminhamentos automáticos)
- `paciente.stts_trat` não é atualizado no método `encerrarEncaminhamento()` — o método existe na entity mas não é chamado no fluxo atual
- `naoLidas` em conversas é sempre `0` — coluna `lida` não existe no DDL atual
- `PREV_FOLLOW` não é atualizado após o disparo do follow-up — o mesmo encaminhamento será reprocessado nas próximas execuções enquanto `STTS_ENCAM = 'ativo'`
- CPF e CRO duplicados retornam 400 (não 409)
- Sem paginação nas listagens (todas retornam registros completos)
- Sem caching nos endpoints de métricas (cada request executa queries agregadas no Oracle)

---

## 14. Documentação relacionada

- `docs/testes-manuais.md` — roteiro de testes ponta a ponta com Postman (inclui setup de Environment + script automático de token)
- `docs/deploy-render.md` — checklist de deploy no Render
- `docs/database/setup_oracle.sql` — DDL completo das tabelas
- `docs/api-collection/nora-backend.json` — coleção Postman v2.1
