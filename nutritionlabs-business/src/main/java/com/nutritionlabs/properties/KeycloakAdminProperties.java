package com.nutritionlabs.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Connection to the Keycloak Admin REST API. The client must be confidential, with the service account enabled and
 * the "manage-users" and "view-realm" roles of the "realm-management" client.
 *
 * @param url          base URL of Keycloak, for example http://localhost:8080/auth
 * @param realm        realm where the users are created
 * @param clientId     client used to get the admin access token (client credentials grant)
 * @param clientSecret secret of the client
 * @param realmRoles   realm roles given to the new users
 */
@ConfigurationProperties(prefix = "nutritionlabs.keycloak")
public record KeycloakAdminProperties(String url, String realm, String clientId, String clientSecret, List<String> realmRoles) {

	public KeycloakAdminProperties {
		realmRoles = realmRoles == null ? List.of() : List.copyOf(realmRoles);
	}
}
