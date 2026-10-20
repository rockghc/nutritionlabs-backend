package com.nutritionlabs.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param requiredRole   Keycloak role (realm or client role) required to call the API
 * @param allowedOrigins origins allowed to call the API from a browser (CORS)
 */
@ConfigurationProperties(prefix = "nutritionlabs.security")
public record SecurityProperties(String requiredRole, List<String> allowedOrigins) {

	public SecurityProperties {
		requiredRole = requiredRole == null || requiredRole.isBlank() ? "user" : requiredRole;
		allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
	}
}
