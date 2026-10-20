package com.nutritionlabs.domain.users.entity;

import java.io.Serializable;
import java.time.LocalTime;

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
@Table(name = "nlabs_dietary_history")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DietaryHistoryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usual_wake_time")
	private LocalTime usualWakeTime;

	@Column(name = "type_diete_description")
	private String typeDieteDescription;

	@Column(name = "allergy_description")
	private String allergyDescription;

	@Column(name = "food_intolerance_description")
	private String foodIntoleranceDescription;

	@Column(name = "nutritional_deficiency_description")
	private String nutritionalDeficiencyDescription;

	@Column(name = "favorite_food_description")
	private String favoriteFoodDescription;

	@Column(name = "disliked_food_description")
	private String dislikedFoodDescription;

	@Column(name = "id_water_intake")
	private Long idWaterIntake;

	@Column(name = "other_information_description")
	private String otherInformationDescription;

	@Column(name = "usual_bed_time")
	private LocalTime usualBedTime;

	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "id_user")
	private UserEntity userEntity;
}
