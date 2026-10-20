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
public class PersonalHistoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long idBowelMovements;
	private String bowelMovementsDescription;
	private Long idSleepQuality;
	private String sleepQualityDescription;
	private Boolean smoker;
	private String smokerDescription;
	private Boolean alcoholConsumption;
	private String alcoholConsumptionDescription;
	private Long idMaritalStatus;
	private String maritalStatusDescription;
	private String physicalActivityDescription;
	private Long idRace;
	private String otherInformationDescription;
}
