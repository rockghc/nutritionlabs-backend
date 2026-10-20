package com.nutritionlabs.domain.users.entity;

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

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
@Entity
@Table(name = "nlabs_personal_history")
public class PersonalHistoryEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "id_bowel_movements")
	private Long idBowelMovements;

	@Column(name = "bowel_movements_description")
	private String bowelMovementsDescription;

	@Column(name = "id_sleep_quality")
	private Long idSleepQuality;

	@Column(name = "sleep_quality_description")
	private String sleepQualityDescription;

	@Column(name = "smoker")
	private Boolean smoker;

	@Column(name = "smoker_description")
	private String smokerDescription;

	@Column(name = "alcohol_consumption")
	private Boolean alcoholConsumption;

	@Column(name = "alcohol_consumption_description")
	private String alcoholConsumptionDescription;

	@Column(name = "id_marital_status")
	private Long idMaritalStatus;

	@Column(name = "marital_status_description")
	private String maritalStatusDescription;

	@Column(name = "physical_activity_description")
	private String physicalActivityDescription;

	@Column(name = "id_race")
	private Long idRace;

	@Column(name = "other_information_description")
	private String otherInformationDescription;

	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "id_user")
	private UserEntity userEntity;
}
