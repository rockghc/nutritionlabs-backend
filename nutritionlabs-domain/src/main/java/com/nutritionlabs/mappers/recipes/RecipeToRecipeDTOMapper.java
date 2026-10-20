package com.nutritionlabs.mappers.recipes;

import java.time.LocalTime;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.recipes.dto.RecipeDTO;
import com.nutritionlabs.domain.recipes.entity.RecipeEntity;

@Mapper
public interface RecipeToRecipeDTOMapper {

	public RecipeToRecipeDTOMapper MAPPER = Mappers.getMapper(RecipeToRecipeDTOMapper.class);

	@Mapping(target = "energy", ignore = true)
	@Mapping(target = "carbohydrate", ignore = true)
	@Mapping(target = "fat", ignore = true)
	@Mapping(target = "protein", ignore = true)
	@Mapping(target = "micronutrientQuantity", ignore = true)
	RecipeDTO recipeEntityToRecipeDTO(RecipeEntity recipeEntity);

	default Optional<LocalTime> wrapLocalTime(LocalTime localTimeValue) {
		return Optional.ofNullable(localTimeValue);
	}
}
