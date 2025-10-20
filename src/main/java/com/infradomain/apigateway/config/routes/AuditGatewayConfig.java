package com.infradomain.apigateway.config.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Audit Log Gateway Config
 *
 * @author Juliane Maran
 * @since 20/10/2025
 */
@Configuration
public class AuditGatewayConfig {

  @Bean
  public RouteLocator authRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("audit-log-service", r -> r.path("/api/v1/audit/**")
        .filters(f -> f.circuitBreaker(config -> config
          .setName("auditCircuitBreaker")
          .setFallbackUri("forward:/fallback/audit")
        ))
        .uri("http://localhost:8091"))
      .build();
  }

}
