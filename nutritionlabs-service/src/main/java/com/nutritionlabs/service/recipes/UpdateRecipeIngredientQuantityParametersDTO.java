package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * New quantity of a recipe ingredient food.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeIngredientQuantityParametersDTO {

	private Long idUnit;
	private Double quantity;
}
