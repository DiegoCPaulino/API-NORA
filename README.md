# Projeto Nora — Backend Java

## Sobre

Backend da plataforma Nora, desenvolvido para a ONG **Turma do Bem** como projeto acadêmico FIAP (ADS). Gerencia triagem odontológica de adolescentes (11–17 anos), encaminhamento para dentistas voluntários e acompanhamento do tratamento. A camada Java é o orquestrador transacional entre bot Telegram (N8N/Gemini), frontend React (Vercel) e banco Oracle FIAP.

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Quarkus 3.34.6 |
| Build | Maven |
| Banco | Oracle (instância FIAP) |
| Persistência | JDBC manual — PreparedStatement, SQL escrito à mão, sem JPA |
| Integração externa | N8N (follow-up webhook) — Apache HttpClient 4.5.14 + Gson 2.13.1 |
| CORS | Filtro manual via ContainerResponseFilter + @Provider |
| Auth | Token opaco UUID, validado via AuthFilter em memória/banco |
| Deploy | Render |
| Health | /q/health via quarkus-smallrye-health |

## Estrutura de pacotes

```
br/com/fiap/nora/
├── entities/     # POJOs de domínio sem anotações de persistência
├── dao/          # JDBC manual — PreparedStatement, SQL em constantes, ResultSet por nome
├── bo/           # Regras compostas, instanciam DAOs por new
├── services/     # Orquestração (AprovacaoTriagemService, MatchService, MLService, FollowUpService)
├── conexoes/     # ConexaoFactory via System.getenv()
├── resources/    # Endpoints JAX-RS
├── filters/      # CorsFilter, AuthFilter
├── dto/          # LoginRequest/Response, ErroResponse, MatchResponse, DTOs de resposta
└── exceptions/   # RegraNegocioException e exceções de negócio
```

## Como rodar localmente

### Pré-requisitos

- Java 21, Maven 3.9+
- Oracle FIAP acessível na rede (porta 1521)
- Pelo menos 1 colaborador cadastrado em TB_COLABORADOR

### Variáveis de ambiente

```bash
ORACLE_USER=seu_usuario_oracle
ORACLE_PASS=sua_senha_oracle
ORACLE_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
N8N_WEBHOOK_FOLLOWUP_URL=               # opcional — ver seção follow-up abaixo
```

Copie `.env.example` para `.env` e preencha. **Nunca commite `.env`.**

### Follow-up automático (opcional)

A aplicação possui um scheduler que roda diariamente às 09:00 e dispara webhook ao N8N para cada encaminhamento com `PREV_FOLLOW <= data_atual` e `STTS_ENCAM = 'ativo'` (inclui follow-ups atrasados).

Configurar `N8N_WEBHOOK_FOLLOWUP_URL` com a URL do webhook N8N para ativar o disparo. Se a variável não estiver definida, o scheduler executa, registra log informativo e encerra sem disparar (modo passivo — aplicação sobe normalmente).

Cada disparo bem-sucedido registra um evento em `TB_ACOMP_EVENTO` com `TIPO_EVENTO = 'followup'` e `ORIGEM = 'sistema'`. Falha em um encaminhamento é isolada — os demais são processados normalmente.

> **Limitação conhecida:** `PREV_FOLLOW` não é atualizado após o disparo. O mesmo encaminhamento será processado novamente nas execuções seguintes enquanto `STTS_ENCAM = 'ativo'`. Para evitar duplicidade, o N8N deve ser configurado para ignorar webhooks repetidos ou o colaborador deve atualizar o status do encaminhamento manualmente.

> Requer a extensão `quarkus-scheduler` no `pom.xml`. Detalhes em `docs/deploy-render.md`.

### Comandos

```bash
./mvnw quarkus:dev                              # dev com live reload
./mvnw package -DskipTests                     # build de produção
java -jar target/quarkus-app/quarkus-run.jar   # executar jar
curl http://localhost:10000/q/health            # health check
```

## Endpoints implementados

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | /auth/login | Login — retorna token opaco | Não |
| GET | /pessoas | Listar pessoas | Sim |
| POST | /pessoas | Cadastrar pessoa | Sim |
| GET | /pessoas/{id} | Buscar pessoa por ID | Sim |
| PUT | /pessoas/{id} | Atualizar pessoa | Sim |
| GET | /pacientes | Listar pacientes aprovados | Sim |
| GET | /pacientes/{id} | Buscar paciente por ID | Sim |
| GET | /dentistas | Listar dentistas | Sim |
| POST | /dentistas | Cadastrar dentista | Sim |
| GET | /dentistas/{id} | Buscar dentista por ID | Sim |
| PUT | /dentistas/{id} | Atualizar dentista | Sim |
| GET | /dentistas/disponiveis | Dentistas com vagas ativas | Sim |
| GET | /triagens | Listar triagens | Sim |
| GET | /triagens/{id} | Buscar triagem por ID | Sim |
| POST | /triagens | Criar triagem (calcula elegibilidade e prioridade) | Sim |
| PUT | /triagens/{id} | Atualizar status/decisão da triagem | Sim |
| **POST** | **/triagens/{id}/aprovar** | **Aprovação transacional atômica** | Sim |
| GET | /encaminhamentos | Listar encaminhamentos | Sim |
| POST | /encaminhamentos | Criar encaminhamento manual | Sim |
| GET | /encaminhamentos/{id} | Buscar encaminhamento por ID | Sim |
| PUT | /encaminhamentos/{id} | Atualizar status/observação do encaminhamento | Sim |
| GET | /conversas | Listar conversas (?contexto= &stts=) | Sim |
| GET | /conversas/{id} | Detalhe de conversa | Sim |
| POST | /conversas | Criar conversa — upsert se já existe ativa | Sim |
| GET | /conversas/{id}/mensagens | Mensagens de uma conversa | Sim |
| POST | /mensagens | Criar mensagem | Sim |
| POST | /acomp_evento | Criar evento de acompanhamento | Sim |
| GET | /encaminhamentos/{id}/eventos | Eventos de um encaminhamento | Sim |
| GET | /metricas/resumo | Totais e taxas: leads, aprovados, encaminhamentos, dentistas, taxas | Sim |
| GET | /metricas/triagens-por-status | Triagens agrupadas por status | Sim |
| GET | /metricas/encaminhamentos-por-prioridade | Encaminhamentos por prioridade | Sim |
| GET | /metricas/leads-por-canal | Leads por canal de origem | Sim |
| GET | /metricas/leads-por-mes | Leads por mês de cadastro (Jan–Dez) | Sim |
| GET | /metricas/regioes | Leads por bairro | Sim |
| POST | /follow-up/executar | Disparar follow-up manualmente (sem aguardar cron) | Sim |
| GET | /q/health | Health check | Não |

## Fluxo de aprovação transacional

`POST /triagens/{id}/aprovar` executa em transação JDBC atômica (`setAutoCommit(false)`):

1. Valida triagem — 404 se não existe, 409 se já aprovada
2. Seleciona dentista disponível via match interno no Oracle — 422 se sem dentista
3. Atualiza triagem (aprovada) e pessoa (aprovada)
4. Insere paciente, migra conversa (cadastro → acomp_paciente)
5. Insere encaminhamento com `match_auto=S`, `dist_km=null`, `prev_follow = hoje + 15 dias`
6. Commit — rollback total em caso de falha em qualquer passo

## Match de dentista

O match de dentista é realizado **internamente** no banco Oracle: o `MatchService` seleciona o primeiro dentista ativo com vagas disponíveis (`encaminhamentos ativos < cap_mensal`). Não há chamada a serviço externo no fluxo de aprovação. `dist_km` é sempre `null` em encaminhamentos gerados automaticamente.

## Documentação adicional

- `docs/api.md` — referência completa de endpoints (request/response/status codes)
- `docs/testes-manuais.md` — roteiro de testes ponta a ponta
- `docs/deploy-render.md` — checklist de deploy no Render
- `docs/database/setup_oracle.sql` — DDL completo
- `docs/api-collection/teste_api-nora` — coleção Postman v2.1
