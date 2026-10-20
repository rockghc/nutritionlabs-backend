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
@Table(name = "nlabs_dietary_history_nutritional_deficiencies")
public class DietaryHistoryNutritionalDeficiencyEntity {

	@EmbeddedId
	private DietaryHistoryNutritionalDeficiencyPkEntity dietaryHistoryNutritionalDeficiencyPkEntity;

	@MapsId("idDietaryHistory")
	@JsonIgnore
	@JoinColumn(name = "id_dietary_history")
	@ManyToOne
	private DietaryHistoryEntity dietaryHistory;

	@MapsId("idNutritionalDeficiency")
	@JsonIgnore
	@JoinColumn(name = "id_nutritional_deficiency")
	@ManyToOne
	private NutritionalDeficiencyEntity nutritionalDeficiencyEntity;
}
