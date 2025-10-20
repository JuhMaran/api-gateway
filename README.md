# 🚀 API Gateway

API Gateway implementado com **Spring Cloud Gateway** e **Eureka Discovery**, usando **Java 25** e **Spring Boot 3.5.6
**.

---

## Funcionalidades

- **Roteamento dinâmico** de APIs via `RouteLocator` (WebFlux).
- **Auditoria global** de requisições enviadas ao serviço de audit.
- **CORS global** configurado via `WebFluxConfigurer`.
- **Log de requisições e respostas** do gateway.
- **Integração com Eureka** para descoberta automática de serviços.
- **Actuator** habilitado para métricas e health checks.
- **Dockerfile** pronto para execução local.

---

## Rotas configuradas

| Serviço                   | Endpoint no Gateway             | URL Interna             |
|---------------------------|---------------------------------|-------------------------|
| Auth Service              | `/api/v1/auth/**`               | `http://localhost:8081` |
| Users Service             | `/api/v1/users/**`              | `http://localhost:8082` |
| Roles Service             | `/api/v1/roles/**`              | `http://localhost:8083` |
| Billing Service           | `/api/v1/billing/**`            | `http://localhost:8084` |
| Finance Service           | `/api/v1/finance/**`            | `http://localhost:8085` |
| Supplier Service          | `/api/v1/suppliers/**`          | `http://localhost:8086` |
| Beer Service              | `/api/v1/beers/**`              | `http://localhost:8087` |
| Product Service           | `/api/v1/products/**`           | `http://localhost:8088` |
| Tap Service               | `/api/v1/taps/**`               | `http://localhost:8089` |
| POS Service               | `/api/v1/sales/**`              | `http://localhost:8090` |
| Audit Log Service         | `/api/v1/audit/**`              | `http://localhost:8091` |
| Container Measure Service | `/api/v1/container-measures/**` | `http://localhost:8093` |

---

## Configuração CORS

Permitido apenas o frontend **http://localhost:4200**.  
Métodos permitidos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`.

---

## Como executar

### Local

```bash
mvn clean spring-boot:run
````

### Docker (local)

```bash
docker build -t api-gateway .
docker run -p 8080:8080 api-gateway
```

---

## Observabilidade

* **Actuator endpoints:**

    * `/actuator/health`
    * `/actuator/info`
    * `/actuator/metrics`

* **Logs:**

    * Console (colorido)
    * Arquivo em `logs/api-gateway.log` (rolling diário)

---

## Dependências principais

* Spring Boot 3.5.6
* Spring Cloud Gateway WebFlux
* Spring Cloud Eureka Client
* Reactor
* Logback
* Actuator

---

## 🧑‍💻 **Desenvolvido por**

**Juliane Maran**
📧 [julianemaran@gmail.com](mailto:julianemaran@gmail.com)
💼 [github.com/JuhMaran](https://github.com/JuhMaran)

---

## 🪪 **Licença**

Distribuído sob licença **Apache 2.0**.
Consulte [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) para mais detalhes.

---

## Observação sobre Execução

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
