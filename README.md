# API Gateway

## Executar a Aplicação

### Usando Docker

1. Acessar o `wsl`
2. Build Manual: `docker build -t api-gateway .`
3. Rodar container: `docker run -p 8080:8080 api-gateway`
4. Logs em tempo real: `docker logs -f <container-id>`

### Rede Docker

Se quiser que os serviços conversem entre si localmente

```bash
docker network create taptrack-net
docker run -d --network taptrack-net --name eureka eureka-server
docker run -d --network taptrack-net --name gateway api-gateway
docker run -d --network taptrack-net --name container-measure container-measure-service
docker run -d --network taptrack-net --name frontend -p 4200:4200 taptrack-frontend
```

---

## Estrutura de Pastas

```
infra-domain/
├── api-gateway/
│   ├── src/main/java/com/infradomain/apigateway/
│   │   ├── ApiGatewayApplication.java
│   │   └── config/
│   │       ├── GatewayConfig.java
│   │       ├── CorsGlobalConfiguration.java
│   │       └── AuditGlobalFilter.java
│   └── resources/
│       ├── application.yml
│       └── logback-spring.xml
└── audit-log-service/
    ├── src/main/java/com/infradomain/auditlog/
    │   ├── AuditLogApplication.java
    │   └── controller/
    │       └── AuditLogController.java
    └── resources/
        └── application.yml
```

## Comunicação entre Serviços

**Frontend → Gateway → Microsserviço**

URL Frontend: http://localhost:4200
URL de rotas padrão: `http://localhost:8080/api/v1/{serviço}/{recurso}`

| Serviço                     | Porta  | Exemplo de endpoint             |
|-----------------------------|--------|---------------------------------|
| `config-service`            | `8888` | `/**`                           |
| `discovery-service`         | `8761` | `/**`                           |
| `api-gateway`               | `8080` | `/**`                           |
| `auth-security-service`     | `8081` | `/api/v1/auth/**`               |
| `users-service`             | `8082` | `/api/v1/users/**`              |
| `roles-service`             | `8083` | `/api/v1/roles/**`              |
| `billing-service`           | `8084` | `/api/v1/billing/**`            |
| `finance-service`           | `8085` | `/api/v1/finance/**`            |
| `supplier-service`          | `8086` | `/api/v1/suppliers/**`          |
| `beer-service`              | `8087` | `/api/v1/beers/**`              |
| `product-service`           | `8088` | `/api/v1/products/**`           |
| `tap-service`               | `8089` | `/api/v1/taps/**`               |
| `pos-service`               | `8090` | `/api/v1/sales/**`              |
| `audit-log-service`         | `8091` | `/api/v1/audit/**`              |
| `container-measure-service` | `8093` | `/api/v1/container-measures/**` |

---

Divisão do _identity-profiles_ em subdomínios:

| Novo serviço        | Responsabilidade principal                            |
|---------------------|-------------------------------------------------------|
| `users-service`     | CRUD de usuários, perfis, senhas                      |
| `roles-service`     | Perfis de acesso, permissões, vínculos usuário ↔ role |
| `audit-log-service` | Registro e consulta de logs de auditoria              |

Sugestão de endpoints:

* users-service → http://localhost:8082/api/v1/users
* roles-service → http://localhost:8083/api/v1/roles
* audit-log → http://localhost:8089/api/v1/audit

---

## Serviço de Auditoria Centralizado (`audit-log-service`)

---

## Caminho Evolutivo

| Etapa | Ação                                                            | Observação                          |
|-------|-----------------------------------------------------------------|-------------------------------------|
| 1     | Implementar Gateway com CORS global e rotas locais              | (feito acima)                       |
| 2     | Dividir `identity-profiles` → `users-service` + `roles-service` | facilita segurança e escalabilidade |
| 3     | Criar `audit-log-service` e integrar via REST                   | depois migrar para mensageria       |
| 4     | Incluir `auth-security-service` (JWT, Keycloak ou OAuth2)       | após microsserviços estáveis        |
| 5     | Migrar URIs locais → `lb://SERVICE-NAME` no Eureka              | cloud-ready                         |
| 6     | Adicionar circuit breaker, retries, rate-limit, logs            | resiliente e observável             |
