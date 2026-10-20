package com.nutritionlabs.domain.reports.dto;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.nutritionlabs.domain.recipes.dto.CookingMethodDTO;
import com.nutritionlabs.domain.recipes.dto.IngredientFoodReportDTO;
import com.nutritionlabs.domain.recipes.dto.NutritionalInformationDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeReportDTO;

import lombok.Getter;

/**
 * Bean read by the RecipeReport JasperReports template. The property names are the fields of the template.
 */
@Getter
public class RecipeReportDataDTO {

	private static final String ALTERNATIVE_INGREDIENT_SEPARATOR = " <style forecolor='#1AB394' isBold='true'>or</style> ";

	private final Long idRecipe;
	private final String name;
	private final Long portions;
	private final ByteArrayInputStream imageArray;
	private final List<List<IngredientFoodReportDTO>> ingredientFoods;
	private final List<String> ingredientFoodsNames;
	private final List<CookingMethodDTO> cookingMethods;
	private final List<NutritionalInformationDTO> nutritionalInformations;

	public RecipeReportDataDTO(RecipeReportDTO recipeReportDTO, byte[] image) {
		this.idRecipe = recipeReportDTO.getIdRecipe();
		this.name = recipeReportDTO.getName();
		this.portions = recipeReportDTO.getPortions();
		this.imageArray = image != null ? new ByteArrayInputStream(image) : null;
		this.ingredientFoods = recipeReportDTO.getIngredientFoods();
		this.cookingMethods = recipeReportDTO.getCookingMethods();
		this.nutritionalInformations = recipeReportDTO.getNutritionalInformations();
		this.ingredientFoodsNames = recipeReportDTO.getIngredientFoods().stream()
				.map(ingredients -> ingredients
						.stream()
						.map(IngredientFoodReportDTO::getName)
						.collect(Collectors.joining(ALTERNATIVE_INGREDIENT_SEPARATOR)))
				.collect(Collectors.toList());
	}
}
