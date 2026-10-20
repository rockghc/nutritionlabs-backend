package com.nutritionlabs.domain.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
@Entity
@Table(name = "nlabs_appointment_clinical_goals")
public class AppointmentClinicalGoals {

	@EmbeddedId
	private AppointmentClinicalGoalPkEntity appointmentClinicalGoalPk;

	@MapsId("idAppointmentInformation")
	@JsonIgnore
	@JoinColumn(name = "id_appointment_information")
	@ManyToOne
	private AppointmentInformationEntity appointmentInformation;

	@MapsId("idClinicalGoal")
	@JsonIgnore
	@JoinColumn(name = "id_clinical_goal")
	@ManyToOne
	private ClinicalGoalEntity clinicalGoal;
}
