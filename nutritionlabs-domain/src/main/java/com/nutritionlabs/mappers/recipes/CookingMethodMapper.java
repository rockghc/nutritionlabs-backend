package com.nutritionlabs.mappers.recipes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.recipes.dto.CookingMethodDTO;
import com.nutritionlabs.domain.recipes.entity.RecipeCookingMethodEntity;

@Mapper
public interface CookingMethodMapper {

	public CookingMethodMapper MAPPER = Mappers.getMapper(CookingMethodMapper.class);

	@Mapping(source = "recipeEntity.id", target = "idRecipe")
	CookingMethodDTO convertToDto(RecipeCookingMethodEntity recipeCookingMethodEntity);
}
