package com.infradomain.apigateway.config.audit;

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

  // Bean único de WebClient para auditoria
  @Bean
  public WebClient auditWebClient(WebClient.Builder builder) {
    return builder
      .baseUrl("http://localhost:8091/api/v1/audit")
      .build();
  }

  // Global Filter de auditoria
  @Bean
  public GlobalFilter auditFilter(WebClient auditWebClient) {
    return (exchange, chain) -> {

      String method = exchange.getRequest().getMethod().name();
      String path = exchange.getRequest().getURI().getPath();

      var event = new AuditEvent("gateway", method, path);

      // Envia evento de auditoria de forma assíncrona
      return auditWebClient.post()
        .bodyValue(event)
        .retrieve()
        .bodyToMono(Void.class)
        .doOnError(ex -> logger.warn("[AUDIT] Falha ao enviar evento: {}", ex.getMessage()))
        .onErrorContinue((ex, obj) -> {
          // Continua mesmo em caso de erro
        })
        .then(chain.filter(exchange));
    };
  }

  // Classe interna para o evento de auditoria
  public record AuditEvent(String source, String method, String path) {
  }

}
