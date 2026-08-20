package com.cloudvandana.crud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "salesforce")
public record SalesforceProperties(String loginUrl, String clientId, String clientSecret, String redirectUri, String apiVersion, String scopes) {}
