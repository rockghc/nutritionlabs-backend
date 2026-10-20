package com.nutritionlabs.domain.food.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodDTO implements Serializable {

	private static final long serialVersionUID = 2515050498276704803L;

	private Long id;
	private String name;
	private Long foodGroup;
	private Long quantity;
	private Double energy;
	private Double carbohydrate;
	private Double fat;
	private Double protein;
	private Double micronutrientQuantity;
	private CatalogLabelDTO source;
	private List<FoodNutrimentDTO> nutriments;
	private List<FoodCommonMeasureDTO> commonMeasures;
}
