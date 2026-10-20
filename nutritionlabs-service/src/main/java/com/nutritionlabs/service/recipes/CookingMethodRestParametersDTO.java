package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cooking method of a recipe.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CookingMethodRestParametersDTO {

	private String name;
}
