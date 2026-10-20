package com.nutritionlabs.dataaccess.recipes;

import org.springframework.data.domain.Page;

import com.nutritionlabs.domain.recipes.dto.RecipeDTO;
import com.nutritionlabs.domain.recipes.dto.RecipesPaginatedByUserParametersDTO;

public interface RecipeCustomRepository {

	Page<RecipeDTO> findRecipesWithNutriments(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO);
}
