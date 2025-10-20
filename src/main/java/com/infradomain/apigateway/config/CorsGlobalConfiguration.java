package com.infradomain.apigateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * api-gateway
 *
 * @author Juliane Maran
 * @since 16/10/2025
 */
@Configuration
public class CorsGlobalConfiguration implements WebFluxConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:4200")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .exposedHeaders("*")
      .allowCredentials(false)
      .maxAge(3600);
  }

//  @Bean
//  public CorsConfigurationSource corsConfigurationSource() {
//    var cors = new CorsConfiguration();
//    cors.setAllowedOrigins(List.of("http://localhost:4200"));
//    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//    cors.setAllowedHeaders(List.of("*"));
//    cors.setExposedHeaders(List.of("*"));
//    cors.setAllowCredentials(false);
//    cors.setMaxAge(3600L);
//
//    var source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", cors);
//    return source;
//  }

}
