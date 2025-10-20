package com.infradomain.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * API Gateway - Logger global de requisições <br>
 * Captura requisições, respostas e erros do gateway
 *
 * @author Juliane Maran
 * @since 19/10/2025
 */
@Component
public class GatewayRequestLogger implements GlobalFilter {

  private static final Logger log = LoggerFactory.getLogger(GatewayRequestLogger.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    // Log da requisição
    log.info("[GATEWAY] Request -> {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());

    return chain.filter(exchange)
      // Log da resposta
      .doOnSuccess(done -> log.info("[GATEWAY] Response status: {}",
        exchange.getResponse().getStatusCode()))
      // Captura falhas do gateway
      .doOnError(error -> log.error("[GATEWAY] Falha no gateway: {} - {}",
        error.getClass().getSimpleName(), error.getMessage()));
  }

}