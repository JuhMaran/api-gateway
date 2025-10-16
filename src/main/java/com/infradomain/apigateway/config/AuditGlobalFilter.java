package com.infradomain.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Filtro global de auditoria
 *
 * @author Juliane Maran
 * @since 16/10/2025
 */
@Configuration
public class AuditGlobalFilter {

  private static final Logger logger = LoggerFactory.getLogger(AuditGlobalFilter.class);

  @Bean
  public GlobalFilter auditFilter(WebClient.Builder webClientBuilder) {
    WebClient client = webClientBuilder
      .baseUrl("http://localhost:8091/api/v1/audit")
      .build();

    return (exchange, chain) -> {

      exchange.getRequest().getMethod();
      String method = exchange.getRequest().getMethod().name();
      String path = exchange.getRequest().getURI().getPath();

      var event = new AuditEvent("gateway", method, path);

      return client.post()
        .bodyValue(event)
        .retrieve()
        .bodyToMono(Void.class)
        .doOnError(ex -> logger.warn("[AUDIT] Falha ao enviar evento: {}", ex.getMessage()))
        .onErrorResume(_ -> chain.filter(exchange))
        .then(chain.filter(exchange));
    };

  }

  public record AuditEvent(String source, String method, String path) {
  }

}
