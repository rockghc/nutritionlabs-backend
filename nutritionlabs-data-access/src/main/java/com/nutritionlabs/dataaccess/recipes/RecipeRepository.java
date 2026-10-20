package com.nutritionlabs.dataaccess.recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.recipes.entity.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long>, RecipeCustomRepository {
}
