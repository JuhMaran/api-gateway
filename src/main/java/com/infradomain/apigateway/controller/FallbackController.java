package com.infradomain.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * api-gateway
 *
 * @author Juliane Maran
 * @since 20/10/2025
 */
@RestController
public class FallbackController {

  @GetMapping("/fallback/auth")
  public ResponseEntity<String> authFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body("Serviço Auth indisponível no momento. Tente novamente mais tarde.");
  }

  @GetMapping("/fallback/users")
  public ResponseEntity<String> userFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body("Serviço Users indisponível no momento. Tente novamente mais tarde.");
  }

  @GetMapping("/fallback/audit")
  public ResponseEntity<String> auditFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body("Serviço de Audit Log indisponível no momento. Tente novamente mais tarde.");
  }

  @GetMapping("/fallback/container-measures")
  public ResponseEntity<String> containerMeasureFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body("Serviço de Container Measures indisponível no momento. Tente novamente mais tarde.");
  }

}
