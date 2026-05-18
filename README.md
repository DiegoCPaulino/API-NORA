# Projeto Nora — Backend Java

## Sobre

Backend da plataforma Nora, desenvolvido para a ONG **Turma do Bem** como projeto acadêmico FIAP (ADS). Gerencia triagem odontológica de adolescentes (11–17 anos), encaminhamento para dentistas voluntários e acompanhamento do tratamento. A camada Java é o orquestrador transacional entre bot Telegram (N8N/Gemini), frontend React (Vercel) e serviços Python publicados no Render.

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Quarkus 3.34.6 |
| Build | Maven |
| Banco | Oracle (instância FIAP) |
| Persistência | JDBC manual — PreparedStatement, SQL escrito à mão, sem JPA |
| Integração externa | Apache HttpClient 4.5.14 + Gson 2.13.1 |
| CORS | Filtro manual via ContainerResponseFilter + @Provider |
| Auth | Token opaco UUID persistido em TB_COLABORADOR_SESSAO |
| Deploy | Render |
| Health | /q/health via quarkus-smallrye-health |

## Estrutura de pacotes

```
br/com/fiap/nora/
├── entities/     # POJOs de domínio sem anotações de persistência
├── dao/          # JDBC manual — PreparedStatement, SQL em constantes, ResultSet por nome
├── bo/           # Regras compostas, instanciam DAOs por new
├── services/     # Integração Python (AprovacaoTriagemService, MatchService, MLService, PythonApiClient)
├── conexoes/     # ConexaoFactory via System.getenv()
├── resources/    # Endpoints JAX-RS
├── filters/      # CorsFilter, AuthFilter
├── dto/          # LoginRequest/Response, ErroResponse, PythonEnvelope, MatchResponse, etc.
└── exceptions/   # RegraNegocioException, PythonApiException
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
API_PYTHON_BASE_URL=https://api-triagens.onrender.com
```

Copie `.env.example` para `.env` e preencha. **Nunca commite `.env`.**

### Comandos

```bash
./mvnw quarkus:dev                              # dev com live reload
./mvnw package -DskipTests                     # build de produção
java -jar target/quarkus-app/quarkus-run.jar   # executar jar
curl http://localhost:8080/q/health             # health check
```

## Endpoints implementados

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | /auth/login | Login — retorna token opaco | Não |
| GET | /pessoas | Listar pessoas | Sim |
| POST | /pessoas | Cadastrar pessoa | Sim |
| GET | /pessoas/{id} | Buscar pessoa por ID | Sim |
| PUT | /pessoas/{id} | Atualizar pessoa | Sim |
| GET | /triagens | Listar triagens | Sim |
| POST | /triagens | Criar triagem (calcula elegibilidade e prioridade) | Sim |
| PUT | /triagens/{id} | Atualizar triagem | Sim |
| **POST** | **/triagens/{id}/aprovar** | **Aprovação transacional atômica** | Sim |
| GET | /conversas | Listar conversas (?contexto= &stts=) | Sim |
| GET | /conversas/{id} | Detalhe de conversa | Sim |
| POST | /conversas | Criar conversa — upsert se já existe ativa | Sim |
| GET | /conversas/{id}/mensagens | Mensagens de uma conversa | Sim |
| POST | /mensagens | Criar mensagem | Sim |
| POST | /acomp_evento | Criar evento de acompanhamento | Sim |
| GET | /encaminhamentos/{id}/eventos | Eventos de um encaminhamento | Sim |
| GET | /metricas/resumo | Totais: pessoas, triagens, pacientes, encaminhamentos, conversas | Sim |
| GET | /metricas/triagens-por-status | Triagens agrupadas por status | Sim |
| GET | /metricas/encaminhamentos-por-prioridade | Encaminhamentos por prioridade | Sim |
| GET | /metricas/leads-por-canal | Leads por canal de origem | Sim |
| GET | /metricas/leads-por-mes | Leads por mês de cadastro | Sim |
| GET | /metricas/regioes | Leads por UF | Sim |
| GET | /q/health | Health check | Não |

## Fluxo de aprovação transacional

`POST /triagens/{id}/aprovar` executa em transação JDBC atômica (`setAutoCommit(false)`):

1. Valida triagem — 404 se não existe, 409 se já aprovada/reprovada
2. Chama API Python `/api/triagens/{id}/sugestao-dentista` — 422 se sem dentista
3. Atualiza triagem (aprovada) e pessoa (aprovada)
4. Insere paciente, migra conversa (cadastro → acomp_paciente)
5. Insere encaminhamento com dados do match e `prev_follow = hoje + 15 dias`
6. Commit — rollback total em caso de falha em qualquer passo

## Integração com API Python

API Python unificada em `https://api-triagens.onrender.com`. Match geográfico usa `GET /api/triagens/{id}/sugestao-dentista` (chamada antes de qualquer INSERT, evitando problema de visibilidade transacional). Resposta em envelope `{ status, code, message, data, erro }`. `MLService` é stub com fallback (endpoint de predição numérica não exposto na API Python atual).

## Limitações conhecidas

- Senha comparada em texto puro (sem bcrypt) — limitação acadêmica declarada
- MLService stub — predição IA não disponível via API Python, usa fallback para nivelUrgIa do payload
- `paciente.stts_trat` não atualizado em `encerrarEncaminhamento()` — pendente
- Conversa inexistente na aprovação gera aviso em log mas não aborta a transação
- `GET /conversas/{id}/mensagens` não retorna 404 se a conversa não existir
- Follow-up automático (`@Scheduled`) não implementado
- Sem paginação nas listagens
- Sem caching nas métricas

## Evolução futura

- Follow-up automático com `@Scheduled` (15 dias pós-encaminhamento)
- Hash de senha com bcrypt
- ExceptionMapper global para padronizar erros sem try/catch repetido
- Paginação com `@QueryParam` offset/limit
- Cache de métricas com TTL curto
- DELETE exposto nos endpoints (DAOs já têm o método)

## Documentação adicional

- `docs/database/setup_oracle.sql` — DDL completo
- `docs/api-collection/nora-backend.json` — coleção Postman v2.1
- `docs/testes-manuais.md` — roteiro de testes ponta a ponta
- `docs/decisoes-tecnicas.md` — decisões técnicas e respostas para banca
- `docs/deploy-render.md` — checklist de deploy no Render
