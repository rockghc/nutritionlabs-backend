package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data of a new Keycloak user. A temporary password must be changed by the user at the first login.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateKeycloakUserParamsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private Boolean temporaryPassword;
}
