package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private UserProfileDTO profile;
}
