package com.nutritionlabs.domain.recipes.dto;

import java.io.Serializable;
import java.util.List;

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
public class IngredientFoodDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Double quantity;
	private Long idFood;
	private IngredientFoodCommonMeasureDTO ingredientCommonMeasure;
	private List<IngredientFoodNutrimentDTO> nutriments;
	private List<IngredientFoodCommonMeasureDTO> commonMeasures;
}
