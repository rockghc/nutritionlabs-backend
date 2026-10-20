package com.nutritionlabs.service.utils;

import java.security.Principal;

public interface SecurityUtils {

	/**
	 * Keycloak username ({@code preferred_username} claim) of the authenticated user.
	 */
	String getUsername(Principal principal);
}
