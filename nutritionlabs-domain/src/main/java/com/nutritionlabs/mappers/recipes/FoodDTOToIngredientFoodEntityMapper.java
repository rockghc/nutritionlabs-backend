package com.nutritionlabs.mappers.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nutritionlabs.domain.food.dto.FoodCommonMeasureDTO;
import com.nutritionlabs.domain.food.dto.FoodDTO;
import com.nutritionlabs.domain.food.dto.FoodNutrimentDTO;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodCommonMeasureEntity;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodEntity;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodNutrimentEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeIngredientEntity;

/**
 * Copies a food into a new ingredient food of a recipe.
 */
public final class FoodDTOToIngredientFoodEntityMapper {

	private static final String DEFAULT_UNIT_LABEL = "grams";

	private FoodDTOToIngredientFoodEntityMapper() {
	}

	/**
	 * @param foodDTO                 food with its nutriments and common measures
	 * @param recipeIngredientEntity  ingredient the food belongs to
	 * @param idCommonMeasureOptional id of the food common measure selected as unit, grams when empty
	 * @param quantity                quantity of the selected unit
	 */
	public static IngredientFoodEntity foodDTOToIngredientFoodEntity(FoodDTO foodDTO, RecipeIngredientEntity recipeIngredientEntity,
			Optional<Long> idCommonMeasureOptional, Double quantity) {
		IngredientFoodEntity ingredientFoodEntity = new IngredientFoodEntity();
		ingredientFoodEntity.setIdFood(foodDTO.getId());
		ingredientFoodEntity.setRealName(foodDTO.getName());
		ingredientFoodEntity.setQuantity(quantity);
		ingredientFoodEntity.setRecipeIngredientEntity(recipeIngredientEntity);
		ingredientFoodEntity.setNutriments(toNutriments(foodDTO.getNutriments(), ingredientFoodEntity));
		ingredientFoodEntity.setCommonMeasures(toCommonMeasures(foodDTO.getCommonMeasures(), ingredientFoodEntity));
		idCommonMeasureOptional.ifPresent(idCommonMeasure -> ingredientFoodEntity
				.getCommonMeasures()
				.stream()
				.filter(commonMeasure -> idCommonMeasure.equals(commonMeasure.getIdExternal()))
				.findFirst()
				.ifPresent(ingredientFoodEntity::setIngredientCommonMeasureEntity));
		String quantityValue = String.valueOf(quantity != null ? quantity.doubleValue() : 1);
		ingredientFoodEntity.setName(quantityValue + " " + getSelectedCommonMeasureLabel(ingredientFoodEntity.getIngredientCommonMeasureEntity())
				+ " of " + ingredientFoodEntity.getRealName());
		return ingredientFoodEntity;
	}

	private static List<IngredientFoodNutrimentEntity> toNutriments(List<FoodNutrimentDTO> nutriments, IngredientFoodEntity ingredientFoodEntity) {
		List<IngredientFoodNutrimentEntity> ingredientNutriments = new ArrayList<>();
		if (nutriments != null) {
			nutriments.forEach(nutriment -> ingredientNutriments.add(IngredientFoodNutrimentEntity.builder()
					.idNutrimentCatalog(nutriment.getIdNutrimentCatalog())
					.quantity(nutriment.getQuantity())
					.ingredientFoodEntity(ingredientFoodEntity)
					.build()));
		}
		return ingredientNutriments;
	}

	private static List<IngredientFoodCommonMeasureEntity> toCommonMeasures(List<FoodCommonMeasureDTO> commonMeasures, IngredientFoodEntity ingredientFoodEntity) {
		List<IngredientFoodCommonMeasureEntity> ingredientCommonMeasures = new ArrayList<>();
		if (commonMeasures != null) {
			commonMeasures.forEach(commonMeasure -> ingredientCommonMeasures.add(IngredientFoodCommonMeasureEntity.builder()
					.idExternal(commonMeasure.getId())
					.singularName(commonMeasure.getSingularName())
					.pluralName(commonMeasure.getPluralName())
					.quantity(commonMeasure.getQuantity())
					.totalGrams(commonMeasure.getTotalGrams())
					.ediblePortion(commonMeasure.getEdiblePortion())
					.ingredientFoodEntity(ingredientFoodEntity)
					.build()));
		}
		return ingredientCommonMeasures;
	}

	private static String getSelectedCommonMeasureLabel(IngredientFoodCommonMeasureEntity ingredientFoodCommonMeasureEntity) {
		if (ingredientFoodCommonMeasureEntity != null && ingredientFoodCommonMeasureEntity.getSingularName() != null) {
			return ingredientFoodCommonMeasureEntity.getSingularName().toLowerCase();
		}
		return DEFAULT_UNIT_LABEL;
	}
}
