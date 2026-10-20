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
public class MedicalHistoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String medicationDescription;
	private String personalHistoryDescription;
	private String familyHistoryDescription;
	private String otherInformationDescription;
	private String diseaseDescription;
}
