package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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
public class UserProfileDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String username;
	private Long idGender;
	private Long idCountry;
	private LocalDate birthdate;
	private String phonePrefix;
	private String mobileNumber;
	private Long idProfession;
	private Long idFeature;
	private String workplaceName;
	private Set<Long> practiceAreaIds;
	private Long idClientsPerMonth;
	private LocalDateTime creationDate;
	private LocalDateTime updateDate;
}
