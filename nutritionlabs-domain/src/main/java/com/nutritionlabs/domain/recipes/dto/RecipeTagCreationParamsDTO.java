package com.nutritionlabs.domain.recipes.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class RecipeTagCreationParamsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idRecipe;
	private Long id;
	private String name;
	private String description;
}
