package com.nutritionlabs.dataaccess.recipes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.recipes.entity.RecipeIngredientEntity;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredientEntity, Long> {

	List<RecipeIngredientEntity> findByRecipeEntity_IdOrderById(Long recipeId);
}
