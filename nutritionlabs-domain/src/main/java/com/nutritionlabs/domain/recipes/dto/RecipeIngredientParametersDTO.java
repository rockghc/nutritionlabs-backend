package com.nutritionlabs.domain.recipes.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientParametersDTO {

	private Long idRecipe;
	@Builder.Default
	private Optional<Long> idRecipeIngredient = Optional.empty();
	private Long idIngredientFood;
	private Long idFood;
	@Builder.Default
	private Optional<Long> idUnit = Optional.empty();
	private Double quantity;
	private String name;
}
