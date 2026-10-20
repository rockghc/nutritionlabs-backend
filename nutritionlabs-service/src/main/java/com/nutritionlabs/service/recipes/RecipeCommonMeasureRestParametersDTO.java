package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Common measure of a recipe.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCommonMeasureRestParametersDTO {

	private String singularName;
	private String pluralName;
	private Double quantity;
	private Double totalGrams;
	private Double ediblePortion;
}
