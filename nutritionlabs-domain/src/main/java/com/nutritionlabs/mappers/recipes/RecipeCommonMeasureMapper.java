package com.nutritionlabs.mappers.recipes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.recipes.dto.RecipeCommonMeasureDTO;
import com.nutritionlabs.domain.recipes.entity.RecipeCommonMeasureEntity;

@Mapper
public interface RecipeCommonMeasureMapper {

	public RecipeCommonMeasureMapper MAPPER = Mappers.getMapper(RecipeCommonMeasureMapper.class);

	@Mapping(source = "recipeEntity.id", target = "idRecipe")
	RecipeCommonMeasureDTO convertToDomain(RecipeCommonMeasureEntity commonMeasureEntity);
}
