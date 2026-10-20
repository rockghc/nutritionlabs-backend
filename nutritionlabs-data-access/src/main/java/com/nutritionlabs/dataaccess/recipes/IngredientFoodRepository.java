package com.nutritionlabs.dataaccess.recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.recipes.entity.IngredientFoodEntity;

public interface IngredientFoodRepository extends JpaRepository<IngredientFoodEntity, Long> {
}
