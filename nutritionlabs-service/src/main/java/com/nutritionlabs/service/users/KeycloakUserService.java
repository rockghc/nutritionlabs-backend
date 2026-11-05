package com.nutritionlabs.service.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.users.KeycloakUserBusinessService;
import com.nutritionlabs.domain.users.dto.CreateKeycloakUserParamsDTO;
import com.nutritionlabs.domain.users.dto.KeycloakUserDTO;
import com.nutritionlabs.service.ResourcePaths;

/**
 * Users of the application, registered in Keycloak.
 */
@RestController
@RequestMapping(ResourcePaths.KEYCLOAK_USER)
public class KeycloakUserService {

	@Autowired
	private KeycloakUserBusinessService keycloakUserBusinessService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public KeycloakUserDTO createUser(@RequestBody CreateKeycloakUserParamsDTO params) {
		return keycloakUserBusinessService.createUser(params);
	}
}
