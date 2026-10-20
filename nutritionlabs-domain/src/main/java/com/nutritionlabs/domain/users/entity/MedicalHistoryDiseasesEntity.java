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
@Table(name = "nlabs_medical_history_diseases")
public class MedicalHistoryDiseasesEntity {

	@EmbeddedId
	private MedicalHistoryDiseasePkEntity medicalHistoryDiseasePkEntity;

	@MapsId("idMedicalHistory")
	@JsonIgnore
	@JoinColumn(name = "id_medical_history")
	@ManyToOne
	private MedicalHistoryEntity medicalHistory;

	@MapsId("idDisease")
	@JsonIgnore
	@JoinColumn(name = "id_disease")
	@ManyToOne
	private DiseaseEntity diseaseEntity;
}
