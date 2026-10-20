package com.nutritionlabs.dataaccess.recipes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.recipes.entity.RecipeTagEntity;

public interface RecipeTagRepository extends JpaRepository<RecipeTagEntity, Long> {

	List<RecipeTagEntity> getDistinctByRecipeEntities_RecipeUserEntity_RecipeUserPkEntity_UsernameOrderByName(String username);

	List<RecipeTagEntity> getByRecipeEntities_IdAndRecipeEntities_RecipeUserEntity_RecipeUserPkEntity_UsernameOrderByName(Long id, String username);
}
