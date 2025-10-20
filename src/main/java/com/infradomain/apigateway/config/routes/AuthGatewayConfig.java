package com.infradomain.apigateway.config.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auth Gateway Config
 *
 * @author Juliane Maran
 * @since 20/10/2025
 */
@Configuration
public class AuthGatewayConfig {

  @Bean
  public RouteLocator authRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("auth-security-service", r -> r.path("/api/v1/auth/**")
        .filters(f -> f.circuitBreaker(config -> config
          .setName("authCircuitBreaker")
          .setFallbackUri("forward:/fallback/auth")
        ))
        .uri("http://localhost:8081"))
      .build();
  }

}
