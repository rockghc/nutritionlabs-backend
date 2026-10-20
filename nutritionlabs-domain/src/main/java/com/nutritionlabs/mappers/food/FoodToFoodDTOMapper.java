package com.nutritionlabs.mappers.food;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.food.dto.CatalogLabelDTO;
import com.nutritionlabs.domain.food.dto.FoodCommonMeasureDTO;
import com.nutritionlabs.domain.food.dto.FoodDTO;
import com.nutritionlabs.domain.food.dto.FoodNutrimentDTO;
import com.nutritionlabs.domain.food.entity.CatalogLabelEntity;
import com.nutritionlabs.domain.food.entity.FoodCommonMeasureEntity;
import com.nutritionlabs.domain.food.entity.FoodEntity;
import com.nutritionlabs.domain.food.entity.FoodNutrimentEntity;

@Mapper
public interface FoodToFoodDTOMapper {

	public FoodToFoodDTOMapper INSTANCE = Mappers.getMapper(FoodToFoodDTOMapper.class);

	@Mapping(target = "energy", ignore = true)
	@Mapping(target = "carbohydrate", ignore = true)
	@Mapping(target = "fat", ignore = true)
	@Mapping(target = "protein", ignore = true)
	@Mapping(target = "micronutrientQuantity", ignore = true)
	FoodDTO foodEntityToFoodDTOWithNutriments(FoodEntity foodEntity);

	List<FoodNutrimentDTO> foodNutrimentsToDTOs(List<FoodNutrimentEntity> foodNutrimentEntities);

	FoodNutrimentDTO foodNutrimentToFoodNutrimentDTO(FoodNutrimentEntity foodNutrimentEntity);

	@Mapping(source = "foodEntity.id", target = "idFood")
	FoodCommonMeasureDTO commonMeasureToCommonMeasureDTO(FoodCommonMeasureEntity commonMeasureEntity);

	CatalogLabelDTO catalogLabelToCatalogLabelDTO(CatalogLabelEntity catalogLabelEntity);
}
