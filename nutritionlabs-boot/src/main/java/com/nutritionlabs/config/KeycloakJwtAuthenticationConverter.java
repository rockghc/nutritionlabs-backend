package com.nutritionlabs.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.nutritionlabs.service.utils.SecurityUtilsImpl;

/**
 * Converts a Keycloak access token into an authentication whose name is the Keycloak username and whose
 * authorities are the realm roles and the client roles of the token, prefixed with "ROLE_".
 */
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private static final String ROLE_PREFIX = "ROLE_";
	private static final String REALM_ACCESS_CLAIM = "realm_access";
	private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
	private static final String ROLES = "roles";

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		String username = jwt.getClaimAsString(SecurityUtilsImpl.USERNAME_CLAIM);
		return new JwtAuthenticationToken(jwt, extractAuthorities(jwt), username != null ? username : jwt.getSubject());
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS_CLAIM);
		addRoles(realmAccess, authorities);
		Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
		if (resourceAccess != null) {
			resourceAccess.values().stream()
					.filter(Map.class::isInstance)
					.forEach(clientAccess -> addRoles((Map<?, ?>) clientAccess, authorities));
		}
		return authorities;
	}

	private void addRoles(Map<?, ?> access, Set<GrantedAuthority> authorities) {
		if (access != null && access.get(ROLES) instanceof Collection<?> roles) {
			roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
		}
	}
}
