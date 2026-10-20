package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Filters of the recipe list.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRestServiceParametersDTO {

	private String searchName;
	private Long category;
}
