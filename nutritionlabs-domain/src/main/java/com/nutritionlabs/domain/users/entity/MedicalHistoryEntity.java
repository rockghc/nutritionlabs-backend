package com.nutritionlabs.domain.users.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nlabs_medical_history")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MedicalHistoryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "medication_description")
	private String medicationDescription;

	@Column(name = "personal_history_description")
	private String personalHistoryDescription;

	@Column(name = "family_history_description")
	private String familyHistoryDescription;

	@Column(name = "other_information_description")
	private String otherInformationDescription;

	@Column(name = "disease_description")
	private String diseaseDescription;

	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "id_user")
	private UserEntity userEntity;
}
