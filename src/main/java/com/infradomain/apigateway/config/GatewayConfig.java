package com.infradomain.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api-gateway
 * <p>
 * Configuração das rotas estáticas do API Gateway
 * Ambiente local (sem autenticação)
 *
 * @author Juliane Maran
 * @since 16/10/2025
 */
@Configuration
public class GatewayConfig {

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("auth-security-service", r -> r.path("/api/v1/auth/**")
        .uri("http://localhost:8081"))
      .route("users-service", r -> r.path("/api/v1/users/**")
        .uri("http://localhost:8082"))
      .route("roles-service", r -> r.path("/api/v1/roles/**")
        .uri("http://localhost:8083"))
      .route("billing-service", r -> r.path("/api/v1/billing/**")
        .uri("http://localhost:8084"))
      .route("finance-service", r -> r.path("/api/v1/finance/**")
        .uri("http://localhost:8085"))
      .route("supplier-service", r -> r.path("/api/v1/suppliers/**")
        .uri("http://localhost:8086"))
      .route("beer-service", r -> r.path("/api/v1/beers/**")
        .uri("http://localhost:8087"))
      .route("product-service", r -> r.path("/api/v1/products/**")
        .uri("http://localhost:8088"))
      .route("tap-service", r -> r.path("/api/v1/taps/**")
        .uri("http://localhost:8089"))
      .route("pos-service", r -> r.path("/api/v1/sales/**")
        .uri("http://localhost:8090"))
      .route("audit-log-service", r -> r.path("/api/v1/audit/**")
        .uri("http://localhost:8091"))
      .build();
  }

}
