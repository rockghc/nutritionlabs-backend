package com.nutritionlabs.service.utils;

import java.security.Principal;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtilsImpl implements SecurityUtils {

	public static final String USERNAME_CLAIM = "preferred_username";

	@Override
	public String getUsername(Principal principal) {
		if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
			String username = jwtAuthenticationToken.getToken().getClaimAsString(USERNAME_CLAIM);
			if (username != null) {
				return username;
			}
		}
		if (principal == null) {
			throw new AuthenticationCredentialsNotFoundException("User not authenticated");
		}
		return principal.getName();
	}
}
