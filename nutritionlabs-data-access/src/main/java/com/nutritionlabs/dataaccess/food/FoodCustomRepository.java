package com.nutritionlabs.dataaccess.food;

import org.springframework.data.domain.Page;

import com.nutritionlabs.domain.food.dto.FoodDTO;
import com.nutritionlabs.domain.food.dto.FoodDomainParametersDTO;

public interface FoodCustomRepository {

	Page<FoodDTO> findFoodsWithNutriments(FoodDomainParametersDTO foodDomainParametersDTO);
}
