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
public class ContainerMeasureGatewayConfig {

  @Bean
  public RouteLocator authRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("container-measure-service", r -> r.path("/api/v1/container-measures/**")
        .filters(f -> f.circuitBreaker(config -> config
          .setName("containerMeasureCircuitBreaker")
          .setFallbackUri("forward:/fallback/container-measures")
        ))
        .uri("http://localhost:8093"))
      .build();
  }

}
