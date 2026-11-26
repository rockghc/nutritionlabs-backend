package com.nutritionlabs.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nutritionlabs.service.ResourcePaths;

/**
 * The API is an OAuth2 resource server: every request needs a Keycloak access token with the required role.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProperties securityProperties) throws Exception {
		String requiredRole = securityProperties.requiredRole();
		http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						// Self-registration: anyone can create a Keycloak user.
						.requestMatchers(HttpMethod.POST, ResourcePaths.KEYCLOAK_USER).permitAll()
						// Only the catalogs of the sign up form are public, the other catalogs need the role.
						.requestMatchers(HttpMethod.GET,
								ResourcePaths.CATALOG_GENDERS,
								ResourcePaths.CATALOG_COUNTRIES,
								ResourcePaths.CATALOG_DIALING_CODES,
								ResourcePaths.CATALOG_PROFESSIONS).permitAll()
						.requestMatchers(
								ResourcePaths.USER + "/**",
								ResourcePaths.CATALOG + "/**",
								ResourcePaths.FOOD + "/**",
								ResourcePaths.RECIPE + "/**",
								ResourcePaths.FILE + "/**",
								ResourcePaths.REPORT + "/**").hasRole(requiredRole)
						.requestMatchers("/error").permitAll()
						.anyRequest().denyAll())
				.oauth2ResourceServer(oauth2 -> oauth2
						.bearerTokenResolver(bearerTokenResolver())
						.jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())));
		return http.build();
	}

	/**
	 * Reads the token from the Authorization header or, for links opened by the browser (images, reports), from
	 * the access_token query parameter.
	 */
	private DefaultBearerTokenResolver bearerTokenResolver() {
		DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
		bearerTokenResolver.setAllowUriQueryParameter(true);
		return bearerTokenResolver;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(SecurityProperties securityProperties) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(securityProperties.allowedOrigins());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("Content-Disposition"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
