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
@Table(name = "nlabs_dietary_history_food_intolerances")
public class DietaryHistoryFoodIntoleranceEntity {

	@EmbeddedId
	private DietaryHistoryFoodIntolerancePkEntity dietaryHistoryFoodIntolerancePkEntity;

	@MapsId("idDietaryHistory")
	@JsonIgnore
	@JoinColumn(name = "id_dietary_history")
	@ManyToOne
	private DietaryHistoryEntity dietaryHistory;

	@MapsId("idFoodIntolerance")
	@JsonIgnore
	@JoinColumn(name = "id_food_intolerance")
	@ManyToOne
	private FoodIntoleranceEntity foodIntoleranceEntity;
}
