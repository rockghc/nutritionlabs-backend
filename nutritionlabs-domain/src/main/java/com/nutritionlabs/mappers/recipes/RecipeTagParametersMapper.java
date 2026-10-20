package com.nutritionlabs.mappers.recipes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.recipes.dto.RecipeTagCreationParamsDTO;
import com.nutritionlabs.domain.recipes.entity.RecipeTagEntity;

@Mapper
public interface RecipeTagParametersMapper {

	public RecipeTagParametersMapper MAPPER = Mappers.getMapper(RecipeTagParametersMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "recipeEntities", ignore = true)
	@Mapping(target = "version", ignore = true)
	RecipeTagEntity tagParametersToRecipeTagEntity(RecipeTagCreationParamsDTO tagCreationParamsDTO);
}
