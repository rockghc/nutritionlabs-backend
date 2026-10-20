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
public class AppointmentInformationDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String reasonAppointment;
	private String clinicalGoalDescription;
	private String expectations;
	private String otherInformation;
	private UserDTO userEntity;
}
