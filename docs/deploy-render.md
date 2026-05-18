# Deploy no Render — Projeto Nora Java

## Configuração do serviço

| Campo | Valor |
|---|---|
| Type | Web Service |
| Environment | Java |
| Build Command | `./mvnw package -DskipTests` |
| Start Command | `java -jar target/quarkus-app/quarkus-run.jar` |
| Health Check Path | `/q/health` |
| Instance Type | Free ou Starter (acadêmico) |

## Variáveis de ambiente obrigatórias

| Variável | Descrição | Exemplo |
|---|---|---|
| ORACLE_USER | Usuário Oracle FIAP | rm123456 |
| ORACLE_PASS | Senha Oracle FIAP | (sua senha) |
| ORACLE_URL | JDBC URL Oracle | jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL |
| API_PYTHON_BASE_URL | URL base da API Python | https://api-triagens.onrender.com |

**Variável opcional:**

| Variável | Descrição |
|---|---|
| N8N_WEBHOOK_FOLLOWUP_URL | (opcional) URL do webhook N8N para follow-up automático. Se ausente, scheduler roda em modo passivo (apenas log, sem POST). |

Configurar no painel Render → Environment → Environment Variables.  
**Nunca commitar credenciais no repositório.**

## Passos de deploy

1. Criar serviço Web Service no Render conectando ao repositório Git
2. Configurar as 4 variáveis de ambiente acima
3. Disparar deploy manual (botão "Deploy latest commit")
4. Aguardar build — deve levar 3–5 min no free tier
5. Verificar logs do Render: deve aparecer `io.quarkus started` sem erros
6. Testar: `GET https://<seu-dominio>.onrender.com/q/health` → esperado 200
7. Testar: `POST https://<seu-dominio>.onrender.com/auth/login`
8. Validar CORS com request vinda de `https://projeto-nora.vercel.app`

## URL pública após deploy

```
https://<nome-do-servico>.onrender.com
```

Anotar aqui após o primeiro deploy bem-sucedido e configurar no frontend React (Vercel).

## Troubleshooting

| Sintoma | Causa provável | Ação |
|---|---|---|
| Build falha: `java: invalid release version` | Java 21 não configurado | Verificar `JAVA_VERSION=21` nas env vars do Render ou confirmar que Render detectou Java 21 pelo `pom.xml` |
| Init timeout / /q/health retorna 503 | Oracle inacessível ou credenciais erradas | Validar ORACLE_URL e credenciais; verificar se IP do Render não está bloqueado pelo firewall FIAP |
| 500 em todas as rotas | Variáveis de ambiente ausentes | Verificar se as 4 variáveis estão configuradas no painel |
| CORS bloqueado no frontend | CorsFilter não está listando a origem correta | Confirmar `filters/CorsFilter.java` com `https://projeto-nora.vercel.app` |
| API Python timeout | `API_PYTHON_BASE_URL` errada ou Python dormindo (free tier) | Aguardar warm-up do Render; verificar URL |
| Scheduler não dispara webhook | `N8N_WEBHOOK_FOLLOWUP_URL` ausente ou vazia | Configurar a variável; verificar logs do Render às 09:00 |

## Observações sobre free tier

- O serviço no free tier hiberna após 15 min sem tráfego — a primeira request após hibernação pode demorar 30–60s
- O Oracle FIAP pode bloquear IPs do Render — validar com o professor se necessário
- A API Python no Render também pode estar hibernando — chamar `GET /api/triagens` nela antes de testar o fluxo de aprovação
