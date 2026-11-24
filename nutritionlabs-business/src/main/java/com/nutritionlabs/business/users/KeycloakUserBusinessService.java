package com.nutritionlabs.business.users;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.domain.users.dto.CreateKeycloakUserParamsDTO;
import com.nutritionlabs.domain.users.dto.KeycloakUserDTO;
import com.nutritionlabs.domain.users.dto.UserProfileDTO;
import com.nutritionlabs.properties.KeycloakAdminProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates users in Keycloak through the Admin REST API, authenticated with the service account of the configured
 * client.
 */
@Service
@Slf4j
public class KeycloakUserBusinessService {

	private static final String ACCESS_TOKEN = "access_token";

	private final KeycloakAdminProperties keycloakProperties;

	private final RestClient restClient;

	private final UserProfileBusinessService userProfileBusinessService;

	public KeycloakUserBusinessService(KeycloakAdminProperties keycloakProperties,
			UserProfileBusinessService userProfileBusinessService) {
		this.keycloakProperties = keycloakProperties;
		this.userProfileBusinessService = userProfileBusinessService;
		this.restClient = RestClient.builder().baseUrl(keycloakProperties.url()).build();
	}

	/**
	 * Creates an enabled user with a password and the configured realm roles, and its row in nlabs_user_profiles.
	 * If the profile cannot be stored, the Keycloak user is deleted so the account is not left without profile.
	 */
	public KeycloakUserDTO createUser(CreateKeycloakUserParamsDTO params) {
		validate(params);
		String accessToken = getAccessToken();
		String userId = postUser(params, accessToken);
		UserProfileDTO profile;
		try {
			assignRealmRoles(userId, accessToken);
			profile = userProfileBusinessService.createProfile(params.getUsername(), userId, params.getProfile());
		} catch (RuntimeException ex) {
			deleteUser(userId, params.getUsername(), accessToken);
			if (ex instanceof DataIntegrityViolationException) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "The profile of the user could not be stored", ex);
			}
			throw ex;
		}
		return KeycloakUserDTO.builder()
				.id(userId)
				.username(params.getUsername())
				.email(params.getEmail())
				.firstName(params.getFirstName())
				.lastName(params.getLastName())
				.profile(profile)
				.build();
	}

	private void validate(CreateKeycloakUserParamsDTO params) {
		if (params == null || !StringUtils.hasText(params.getUsername())) {
			throw new IllegalArgumentException("The username is required");
		}
		if (!StringUtils.hasText(params.getPassword())) {
			throw new IllegalArgumentException("The password is required");
		}
		userProfileBusinessService.validate(params.getProfile());
		if (userProfileBusinessService.existsProfile(params.getUsername())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with the same username already exists");
		}
	}

	/**
	 * Rollback of a Keycloak user whose creation could not be completed.
	 */
	private void deleteUser(String userId, String username, String accessToken) {
		try {
			restClient.delete()
					.uri("/admin/realms/{realm}/users/{id}", keycloakProperties.realm(), userId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientException ex) {
			log.error("The Keycloak user {} ({}) could not be deleted after a failed creation, delete it manually",
					username, userId, ex);
		}
	}

	private String getAccessToken() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "client_credentials");
		form.add("client_id", keycloakProperties.clientId());
		form.add("client_secret", keycloakProperties.clientSecret());
		try {
			Map<?, ?> token = restClient.post()
					.uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(form)
					.retrieve()
					.body(Map.class);
			if (token == null || token.get(ACCESS_TOKEN) == null) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak did not return an access token");
			}
			return String.valueOf(token.get(ACCESS_TOKEN));
		} catch (RestClientException ex) {
			log.error("Could not get the Keycloak admin access token", ex);
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not authenticate with Keycloak", ex);
		}
	}

	/**
	 * @return the id of the new user, last segment of the Location header
	 */
	private String postUser(CreateKeycloakUserParamsDTO params, String accessToken) {
		Map<String, Object> credential = Map.of(
				"type", "password",
				"value", params.getPassword(),
				"temporary", Boolean.TRUE.equals(params.getTemporaryPassword()));
		Map<String, Object> user = new HashMap<>();
		user.put("username", params.getUsername());
		user.put("email", params.getEmail());
		user.put("firstName", params.getFirstName());
		user.put("lastName", params.getLastName());
		user.put("enabled", true);
		user.put("credentials", List.of(credential));
		try {
			URI location = restClient.post()
					.uri("/admin/realms/{realm}/users", keycloakProperties.realm())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.body(user)
					.retrieve()
					.toBodilessEntity()
					.getHeaders()
					.getLocation();
			if (location == null) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak did not return the id of the user");
			}
			String path = location.getPath();
			return path.substring(path.lastIndexOf('/') + 1);
		} catch (HttpClientErrorException.Conflict ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with the same username or email already exists", ex);
		} catch (HttpClientErrorException.BadRequest ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getResponseBodyAsString(), ex);
		} catch (RestClientException ex) {
			log.error("Could not create the Keycloak user {}", params.getUsername(), ex);
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not create the user in Keycloak", ex);
		}
	}

	private void assignRealmRoles(String userId, String accessToken) {
		if (keycloakProperties.realmRoles().isEmpty()) {
			return;
		}
		try {
			List<?> roles = keycloakProperties.realmRoles()
					.stream()
					.map(role -> restClient.get()
							.uri("/admin/realms/{realm}/roles/{role}", keycloakProperties.realm(), role)
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
							.retrieve()
							.body(Map.class))
					.toList();
			restClient.post()
					.uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", keycloakProperties.realm(), userId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.body(roles)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientException ex) {
			log.error("The Keycloak user {} was created but its roles could not be assigned", userId, ex);
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "The user was created but its roles could not be assigned", ex);
		}
	}
}
