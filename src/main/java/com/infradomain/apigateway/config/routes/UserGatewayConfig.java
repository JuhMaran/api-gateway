package com.infradomain.apigateway.config.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User Gateway Config
 *
 * @author Juliane Maran
 * @since 20/10/2025
 */
@Configuration
public class UserGatewayConfig {

  @Bean
  public RouteLocator authRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("users-service", r -> r.path("/api/v1/users/**")
        .filters(f -> f.circuitBreaker(config -> config
          .setName("userCircuitBreaker")
          .setFallbackUri("forward:/fallback/users")
        ))
        .uri("http://localhost:8082"))
      .build();
  }

}
