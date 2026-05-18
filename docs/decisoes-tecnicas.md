# Decisões técnicas — Projeto Nora Java

## 1. Eixo acadêmico — padrão das aulas FIAP (piso técnico)

Todas as escolhas abaixo espelham o ZIP de aulas analisado (`AlunoDao`, `ProjetoAPI`, `aluno-integracao-quarkus`):

- **JDBC manual com PreparedStatement** em vez de JPA/Hibernate/Panache — paradigma da disciplina
- **DAO/BO/Resource** em vez de Repository/Service enterprise — nomenclatura das aulas
- **Entities POJO** sem anotações de persistência (`@Entity`, `@Id`, `@Column`)
- **Validação manual via `if`** em vez de Bean Validation (`@NotNull`, `@Valid`)
- **Sem `@Inject` espalhado** — BOs e DAOs instanciados por `new`
- **CORS via `@Provider ContainerResponseFilter`** — padrão literal das aulas
- **HttpClient + Gson para integração externa** — padrão do `ViaCepService` das aulas
- **Entities em snake_case para DTOs Python** sem `@SerializedName`

## 2. Extensões aceitáveis (além das aulas, com justificativa)

| Extensão | Justificativa |
|---|---|
| Variáveis de ambiente para credenciais Oracle | Obrigatório para deploy seguro no Render |
| `quarkus-smallrye-health` | Exigência do Render para health check |
| `try-with-resources` em PreparedStatement | Boa prática JDBC, não muda paradigma |
| ResultSet mapeado por nome de coluna | Mais legível; índice por posição é frágil a mudanças de coluna |
| `private static final String SQL_*` em DAOs | Evita duplicação; nenhum impacto arquitetural |
| `LocalDate`/`LocalDateTime` em vez de `java.util.Date` | API moderna, amplamente usada em Java 21 |
| **Transação manual JDBC** no fluxo de aprovação | Obrigatória para atomicidade; `setAutoCommit(false)` + `commit`/`rollback` |
| **Token opaco UUID** em vez de JWT | Mais simples que SmallRye JWT; risco menor de implementação incorreta |
| 3 DTOs justificados (`LoginRequest`, `LoginResponse`, `ErroResponse`) | Resolvem problemas concretos — sem eles o contrato fica impreciso |
| `PythonEnvelope<T>` genérico + `MatchResponse`/`MLResponse` | Necessários para desserializar envelope da API Python |
| Padrão de erro com prefixos (`NAO_ENCONTRADA:`, `REGRA:`, `JA_PROCESSADA:`) | Substitui subclasses de exceção — mapeamento simples no Resource |
| `MetricasDao` dedicado para agregações | Evita poluir DAOs de entidade com queries de métricas cruzadas |
| Filtro em memória para listagem de conversas por contexto/status | Volume acadêmico baixo; query dinâmica seria overengineering |

## 3. O que foi deliberadamente evitado

- JPA, Hibernate, Panache, `@Transactional` declarativo
- `@Inject` e `@ApplicationScoped` em BOs/DAOs/Services
- `quarkus-rest-client`, MicroProfile REST Client, WebClient reativo
- SmallRye Fault Tolerance, Circuit Breaker, retry com backoff exponencial
- Bean Validation (`@NotNull`, `@Valid`, `@Pattern`)
- Lombok, MapStruct, `record`, sealed classes
- DTOs especializados para cada entidade (ex: `PessoaRequest`, `PessoaResponse`)
- ExceptionMapper global JAX-RS
- Padrões de design sofisticados (Strategy, Factory abstrato, Builder, Specification)
- Arquitetura hexagonal, DDD pesado (`domain/`, `infrastructure/`, `application/`)
- Testcontainers, Docker, docker-compose, GitHub Actions, CI/CD
- HTTPS/mTLS no código Java
- OpenAPI/Swagger automático
- `@Scheduled` para follow-up (planejado como próxima evolução)

## 4. Decisão arquitetural central — fluxo de aprovação

O problema principal era: como chamar a API Python **antes** de commitar o paciente no Oracle, já que o Python consulta o Oracle em conexão separada e não enxerga dados não commitados?

**Solução:** chamar `GET /api/triagens/{idTriagem}/sugestao-dentista` (a triagem já existe e foi commitada em etapa anterior) **antes** de qualquer INSERT da aprovação. Isso garante que o Python encontra a triagem, e que o Java só insere paciente/encaminhamento após receber um dentista válido.

**Fluxo da transação:**
1. Validate triagem → 2. Chama Python (fora da transação, usando idTriagem existente) → 3. Valida resposta → 4. Inicia escritas → 5. Commit

## 5. Pendências e limitações conhecidas

| # | Pendência | Impacto | Decisão atual | Evolução proposta |
|---|---|---|---|---|
| 1 | Senha em texto puro | Segurança real zero | Comparação direta em AuthBO | bcrypt ou Argon2 |
| 2 | `paciente.stts_trat` não atualizado em `encerrarEncaminhamento()` | CLAUDE.md §9.4 | Método existe na entity mas não é chamado no fluxo | Chamar no contexto de encerramento de encaminhamento |
| 3 | Conversa inexistente na aprovação | Aviso em log, transação prossegue | `AprovacaoTriagemService` loga `System.err` e continua | Tornar obrigatório ou criar conversa automaticamente |
| 4 | `GET /conversas/{id}/mensagens` sem 404 | Retorna lista vazia mesmo se conversa não existe | Comportamento conservador | Validar existência da conversa antes de listar |
| 5 | `tipoMensagem` em AcompEvento sem validação na BO | Banco rejeita via constraint Oracle | Constraint `CHK_ACOMP_MSG` cobre o caso | Adicionar validação explícita na BO para erro 422 amigável |
| 6 | MLService stub | Predição IA não ocorre | Usa nivelUrgIa do payload como fallback | Implementar quando API Python expuser endpoint numérico |
| 7 | Sem caching de métricas | Cada request faz queries agregadas no Oracle | Aceitável para volume acadêmico | Caffeine ou cache em memória com TTL |
| 8 | Follow-up automático ausente | Pacientes não recebem lembrete automático | Planejado como bônus posterior | `@Scheduled` + endpoint de follow-up |
| 9 | Sem paginação | Todas as listagens retornam todos os registros | Aceitável para volume acadêmico | `@QueryParam offset` e `limit` |

## 6. Respostas preparadas para banca

**"Por que não usaram JPA/Hibernate?"**
Padrão explícito das aulas FIAP da disciplina. JDBC com PreparedStatement foi o paradigma ensinado; JPA mudaria completamente o paradigma da camada de persistência. CLAUDE.md §4.3 classifica JPA como "avançado demais" para o escopo acadêmico.

**"Por que token opaco em vez de JWT?"**
JWT completo exige SmallRye JWT, RBAC declarativo, refresh token e, idealmente, bcrypt para senha — tudo fora do repertório das aulas. O token opaco (UUID em tabela) resolve autenticação com código compreensível, sem dependências adicionais.

**"Por que MLService é stub?"**
A API Python publicada no Render (`api-triagens.onrender.com`) não expõe endpoint de predição numérica documentado. O backend aceita `nivelUrgIa` no payload como fallback, mantendo o sistema funcional. Quando o endpoint Python estiver disponível, basta descomentar/implementar o `MLService.predizerUrgencia()`.

**"Como garantem atomicidade na aprovação?"**
`setAutoCommit(false)` + `commit()` somente após todos os passos bem-sucedidos. `catch (Exception)` dispara `rollback()` em qualquer falha, incluindo timeout da API Python. A conexão única é distribuída para todos os DAOs envolvidos via construtor `(Connection conn)`.

**"Onde está o CORS configurado?"**
`filters/CorsFilter.java` — `ContainerResponseFilter` com `@Provider`. Origens restritas: `http://localhost:5173` (dev) e `https://projeto-nora.vercel.app` (prod). Métodos: GET, POST, PUT, DELETE, OPTIONS.

**"Onde está o health check?"**
`/q/health` via extensão `quarkus-smallrye-health` no `pom.xml`. Retorna `{"status":"UP"}` quando Oracle está acessível.

**"O que é o `PythonEnvelope<T>`?"**
DTO genérico que espelha o envelope padrão da API Python: `{ status, code, message, data, erro }`. Usa `TypeToken` do Gson para desserializar o campo `data` no tipo correto (`MatchResponse`, `MLResponse`, etc.).

**"Por que `GET /api/triagens/{id}/sugestao-dentista` em vez de `/api/pacientes/{id}/...`?"**
Problema de visibilidade transacional: o paciente é criado dentro da transação Java (não commitada ainda) e o Python consulta o Oracle em conexão separada, portanto não o enxerga. A triagem já existe antes da aprovação — chamá-la pelo ID da triagem garante que o Python encontra os dados.
