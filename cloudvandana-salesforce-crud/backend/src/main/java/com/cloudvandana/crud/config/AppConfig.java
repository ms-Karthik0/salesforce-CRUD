package com.cloudvandana.crud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {
  @Bean
  RestClient restClient(RestClient.Builder builder) { return builder.build(); }

  @Bean
  WebMvcConfigurer corsConfigurer(@Value("${app.allowed-origin}") String origin) {
    return new WebMvcConfigurer() {
      @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(origin).allowedMethods("GET","POST","PATCH","DELETE","OPTIONS").allowCredentials(true);
      }
    };
  }
}
