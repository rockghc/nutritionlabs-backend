package com.nutritionlabs.domain.recipes.dto;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class RecipeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String description;
	private LocalTime preparationTime;
	private Optional<LocalTime> totalTime;
	private Double weight;
	private Integer weightUnit;
	private Integer portions;
	private String imageName;
	private Double energy;
	private Double carbohydrate;
	private Double fat;
	private Double protein;
	private Boolean isPrivate;
	private Double micronutrientQuantity;
}
