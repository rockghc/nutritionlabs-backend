package com.nutritionlabs.domain.users.dto;

import java.io.Serializable;
import java.time.LocalTime;

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
public class DietaryHistoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private LocalTime usualWakeTime;
	private String typeDieteDescription;
	private String allergyDescription;
	private String foodIntoleranceDescription;
	private String nutritionalDeficiencyDescription;
	private String favoriteFoodDescription;
	private String dislikedFoodDescription;
	private Long idWaterIntake;
	private String otherInformationDescription;
	private LocalTime usualBedTime;
}
