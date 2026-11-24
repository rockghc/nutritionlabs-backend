package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data of the "complete-profile" interface. Every value is optional when the user is created.
 * The ids are ids of nlabs_catalog_details.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileParamsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idGender;
	private Long idCountry;
	private LocalDate birthdate;
	private String phonePrefix;
	private String mobileNumber;
	private Long idProfession;
}
