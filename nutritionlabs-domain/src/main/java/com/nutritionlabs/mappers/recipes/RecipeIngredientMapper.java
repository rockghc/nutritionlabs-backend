package com.nutritionlabs.mappers.recipes;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.recipes.dto.IngredientFoodCommonMeasureDTO;
import com.nutritionlabs.domain.recipes.dto.IngredientFoodDTO;
import com.nutritionlabs.domain.recipes.dto.IngredientFoodNutrimentDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeIngredientDTO;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodCommonMeasureEntity;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodEntity;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodNutrimentEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeIngredientEntity;

@Mapper
public interface RecipeIngredientMapper {

	public RecipeIngredientMapper MAPPER = Mappers.getMapper(RecipeIngredientMapper.class);

	@Mapping(target = "ingredientFoods", source = "ingredientFoodEntities")
	@Mapping(target = "idRecipe", source = "recipeEntity.id")
	RecipeIngredientDTO recipeIngredientEntityToDTOs(RecipeIngredientEntity recipeIngredientEntity);

	List<IngredientFoodDTO> ingredientFoodsEntitiesToDTO(List<IngredientFoodEntity> ingredientFoodEntities);

	@Mapping(target = "ingredientCommonMeasure", source = "ingredientCommonMeasureEntity")
	IngredientFoodDTO ingredientFoodEntityToDTO(IngredientFoodEntity ingredientFoodEntity);

	List<IngredientFoodNutrimentDTO> ingredientFoodNutrimentEntitiesToDTOs(List<IngredientFoodNutrimentEntity> nutriments);

	IngredientFoodNutrimentDTO ingredientFoodNutrimentEntityToDTO(IngredientFoodNutrimentEntity ingredientFoodNutrimentEntity);

	List<IngredientFoodCommonMeasureDTO> ingredientFoodCommonMeasureEntitiesToDTOs(List<IngredientFoodCommonMeasureEntity> commonMeasures);

	IngredientFoodCommonMeasureDTO ingredientFoodCommonMeasureEntityToDTO(IngredientFoodCommonMeasureEntity ingredientFoodCommonMeasureEntity);
}
