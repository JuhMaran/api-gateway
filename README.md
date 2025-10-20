# API Gateway

**Versão:** 1.0.0  
**Stack:** Java 25 · Spring Boot 3.5.6 · Spring Cloud 2025.0.0 · WebFlux · Resilience4j · Caffeine Cache · Prometheus

---

## Visão Geral

O **API Gateway** atua como a **porta de entrada única** para o ecossistema de microsserviços da plataforma **TapTrack
Systems**, centralizando:

- Roteamento inteligente com **Spring Cloud Gateway (WebFlux)**
- Descoberta de serviços com **Eureka Client**
- Configuração de **CORS dinâmico** por ambiente
- **Filtros globais** de auditoria e logging
- **Circuit Breaker + Fallback** via Resilience4j
- **Caffeine Cache** para otimizar o LoadBalancer
- **Métricas Prometheus** e monitoramento via Actuator

---

## Estrutura de Diretórios

```
src/main/java/com/infradomain/apigateway/
├── ApiGatewayApplication.java
├── config/
│   ├── CacheConfig.java               # Configuração do Caffeine Cache
│   ├── CorsConfig.java                # CORS dinâmico via application.yml
│   ├── WebClientConfig.java           # Bean singleton de WebClient
│   ├── GlobalFilterConfig.java        # Registro de filtros globais (ex: auditoria)
│   ├── gateway/                       # Rotas organizadas por domínio/serviço
│   │   ├── AuthGatewayConfig.java
│   │   ├── UserGatewayConfig.java
│   │   ├── AuditGatewayConfig.java
│   │   ├── ContainerMeasureGatewayConfig.java
│   │   └── ...
│   └── resilience/                    # Resiliência e fallback centralizados
│       ├── CircuitBreakerConfig.java  # Configura Resilience4j global
│       └── FallbackController.java    # Endpoint para fallback
├── filter/
│   ├── AuditGlobalFilter.java         # Envia eventos de auditoria
│   └── GatewayRequestLogger.java      # Loga requisições e respostas
├── controller/
│   └── HealthController.java          # Endpoint de health check
├── resources/
│   ├── application.yml                # Configuração base (dev)
│   ├── application-prod.yml           # Configuração de produção
│   └── logback-spring.xml             # Configuração de logs
└── Dockerfile
````

---

## Configurações Principais

### 1. CORS Dinâmico

Definido via `application.yml`, permitindo **origens diferentes por ambiente**.

```yaml
cors:
  allowed-origins:
    - http://localhost:4200
````

Em `application-prod.yml`:

```yaml
cors:
  allowed-origins:
    - https://app.taptrack.com
```

```java

@Configuration
public class CorsConfig implements WebFluxConfigurer {
  @Value("${cors.allowed-origins}")
  private List<String> allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins(allowedOrigins.toArray(new String[ 0 ]))
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .exposedHeaders("*");
  }
}
```

---

### 2. Circuit Breaker + Fallback (Resilience4j)

Configuração central em `/config/resilience/CircuitBreakerConfig.java`.

```
resilience4j:
circuitbreaker:
instances:
auditService:
slidingWindowSize:10
minimumNumberOfCalls:5
failureRateThreshold:50
waitDurationInOpenState:10s
```

Fallback padrão:

```java

@RestController
@RequestMapping("/fallback")
public class FallbackController {
  @GetMapping("/{service}")
  public Mono<ResponseEntity<String>> fallback(@PathVariable String service) {
    return Mono.just(ResponseEntity
      .status(HttpStatus.SERVICE_UNAVAILABLE)
      .body("Serviço temporariamente indisponível: " + service));
  }
}
```

---

### 3. Cache com Caffeine

Aumenta a performance do **Spring Cloud LoadBalancer**, evitando sobrecarga de descoberta via Eureka.

```java

@Configuration
public class CacheConfig {
  @Bean
  public Caffeine<Object, Object> caffeineConfig() {
    return Caffeine.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .maximumSize(1000);
  }

  @Bean
  public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(caffeine);
    return cacheManager;
  }
}
```

---

### 4. WebClient Global

`WebClient` configurado como **bean singleton** para uso em filtros (como auditoria).

```java

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder()
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }
}
```

---

### 5. Auditoria e Logging

**AuditGlobalFilter.java**

```java

@Bean
public GlobalFilter auditFilter(WebClient.Builder webClientBuilder) {
  WebClient client = webClientBuilder.baseUrl("http://localhost:8091/api/v1/audit").build();
  return (exchange, chain) -> {
    var event = new AuditEvent("gateway", exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getPath());
    return client.post()
      .bodyValue(event)
      .retrieve()
      .bodyToMono(Void.class)
      .doOnError(ex -> log.warn("[AUDIT] Falha ao enviar evento: {}", ex.getMessage()))
      .onErrorResume(e -> chain.filter(exchange))
      .then(chain.filter(exchange));
  };
}
```

**GatewayRequestLogger.java**

```java

@Component
public class GatewayRequestLogger implements GlobalFilter {
  private static final Logger log = LoggerFactory.getLogger(GatewayRequestLogger.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("[GATEWAY] {} -> {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
    return chain.filter(exchange)
      .doOnSuccess(done -> log.info("[GATEWAY] Response: {}", exchange.getResponse().getStatusCode()))
      .doOnError(ex -> log.error("[GATEWAY] Erro no fluxo: {}", ex.getMessage()));
  }
}
```

---

## Rotas Modulares

Cada módulo tem sua configuração em `/config/gateway`.

Exemplo: `AuditGatewayConfig.java`

```java

@Configuration
public class AuditGatewayConfig {
  @Bean
  public RouteLocator auditRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("audit-log-service", r -> r.path("/api/v1/audit/**")
        .filters(f -> f.circuitBreaker(c -> c
          .setName("auditService")
          .setFallbackUri("forward:/fallback/audit")))
        .uri("http://localhost:8091"))
      .build();
  }
}
```

---

## **Build e Execução**

### Local

```bash
mvn clean package -DskipTests
java -jar target/api-gateway-1.0.0.jar
```

### Docker**

```bash
docker build -t api-gateway .
docker run -p 8080:8080 api-gateway
```

---

## Boas Práticas Adotadas

✅ Configuração modular (por domínio de serviço)
✅ Evita propriedades depreciadas (`RouteDefinition` via Java config)
✅ Beans reutilizáveis (`WebClient`, `CacheManager`)
✅ Filtros globais bem definidos e desacoplados
✅ Resiliência com fallback centralizado
✅ Cache de descoberta otimizado com Caffeine
✅ Logging estruturado (Logback com rotação diária)
✅ Documentação e padronização corporativa

---

## Observabilidade e Monitoramento

* `/actuator/health` → Verifica integridade do gateway
* `/actuator/metrics` → Métricas internas (via Micrometer)
* `/actuator/prometheus` → Exporta métricas para Prometheus
* `/actuator/gateway/routes` → Lista rotas ativas

---

## Autoria

**Desenvolvido por:** [Juliane Maran](https://github.com/JuhMaran)
**Organização:** TapTrack Systems
**Licença:** [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

---

> “Um gateway bem projetado é o ponto de entrada da arquitetura e o primeiro passo para a resiliência.” 💡

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

---

## Estrutura de Pacotes

```text
src/main/java/com/infradomain/apigateway/
├── ApiGatewayApplication.java
├── config/
│   ├── CacheConfig.java                 # Configuração do Caffeine Cache
│   ├── CorsConfig.java                  # CORS dinâmico via application.yml
│   ├── WebClientConfig.java             # Bean singleton de WebClient
│   ├── GlobalFilterConfig.java          # Registro de filtros globais (ex: auditoria)
│   ├── gateway/                         # Rotas organizadas por domínio/serviço
│   │   ├── AuthGatewayConfig.java
│   │   ├── UserGatewayConfig.java
│   │   ├── AuditGatewayConfig.java
│   │   ├── ContainerMeasureGatewayConfig.java
│   │   └── ...
│   └── resilience/                      # Configurações de resiliência e fallback
│       ├── CircuitBreakerConfig.java    # Configura parâmetros globais do Resilience4j
│       └── FallbackController.java      # Controlador de fallback dos serviços
├── filter/
│   ├── AuditGlobalFilter.java
│   ├── LoggingFilter.java
│   └── AuthenticationFilter.java
├── util/
│   └── RequestUtils.java
└── controller/
    └── HealthController.java
```

### Resumo

| Diretório              | Responsabilidade                                   | Exemplos                                       |
|------------------------|----------------------------------------------------|------------------------------------------------|
| **config/**            | Configurações globais e beans reutilizáveis        | `CorsConfig`, `WebClientConfig`, `CacheConfig` |
| **config/gateway/**    | Rotas de cada microserviço com filtros específicos | `UserGatewayConfig`, `AuditGatewayConfig`      |
| **config/resilience/** | Resiliência e fallback centralizados               | `CircuitBreakerConfig`, `FallbackController`   |
| **filter/**            | Filtros globais e cross-cutting concerns           | `AuditGlobalFilter`, `LoggingFilter`           |
| **controller/**        | Endpoints internos do Gateway                      | `/health`, `/fallback`                         |
| **util/**              | Classes auxiliares, parsing, headers, logs, etc.   | `RequestUtils`                                 |

---