package com.nutritionlabs.domain.common;

/**
 * Entity type names sent by the front end in {@link com.nutritionlabs.domain.common.dto.SingleValueDTO#getType()}
 * and in the image uploads.
 */
public final class EntityTypes {

	public static final String USER = "com.nutritionlabs.domain.users.entity.UserEntity";

	public static final String RECIPE = "com.nutritionlabs.domain.recipes.entity.RecipeEntity";

	private EntityTypes() {
	}
}
