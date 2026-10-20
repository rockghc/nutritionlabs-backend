package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * New name of a recipe ingredient food.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeIngredientNameParametersDTO {

	private String name;
}
