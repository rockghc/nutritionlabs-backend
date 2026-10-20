package com.nutritionlabs.domain.food.dto;

import java.io.Serializable;

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
public class FoodCommonMeasureDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long idFood;
	private String singularName;
	private String pluralName;
	private Double quantity;
	private Double totalGrams;
	private Double ediblePortion;
}
