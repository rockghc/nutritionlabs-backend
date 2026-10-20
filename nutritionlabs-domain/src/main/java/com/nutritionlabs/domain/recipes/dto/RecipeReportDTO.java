package com.nutritionlabs.domain.recipes.dto;

import java.io.Serializable;
import java.util.List;

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
public class RecipeReportDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idRecipe;
	private String name;
	private Long portions;
	private byte[] image;
	private List<List<IngredientFoodReportDTO>> ingredientFoods;
	private List<CookingMethodDTO> cookingMethods;
	private List<NutritionalInformationDTO> nutritionalInformations;
}
