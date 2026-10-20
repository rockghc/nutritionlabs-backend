package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Food added to a recipe.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeIngredientParametersDTO {

	private Long idUnit;
	private Double quantity;
	private Long idFood;
}
