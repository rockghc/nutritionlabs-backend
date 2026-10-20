package com.nutritionlabs.domain.users.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@Setter(value = AccessLevel.PUBLIC)
@Getter
public class AppointmentClinicalGoalPkEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_appointment_information")
	private Long idAppointmentInformation;

	@Column(name = "id_clinical_goal")
	private Long idClinicalGoal;
}
