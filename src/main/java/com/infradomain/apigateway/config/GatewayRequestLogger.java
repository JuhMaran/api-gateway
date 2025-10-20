package com.infradomain.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * api-gateway
 *
 * @author Juliane Maran
 * @since 19/10/2025
 */
@Component
public class GatewayRequestLogger implements GlobalFilter {

  private static final Logger log = LoggerFactory.getLogger(GatewayRequestLogger.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("[GATEWAY] {} -> {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
    return chain.filter(exchange).doOnSuccess((done) ->
      log.info("[GATEWAY] Response status: {}", exchange.getResponse().getStatusCode()));
  }

}