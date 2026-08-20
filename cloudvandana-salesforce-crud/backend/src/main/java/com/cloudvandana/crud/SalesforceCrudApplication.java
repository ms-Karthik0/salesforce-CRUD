package com.cloudvandana.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SalesforceCrudApplication {
  public static void main(String[] args) {
    SpringApplication.run(SalesforceCrudApplication.class, args);
  }
}
