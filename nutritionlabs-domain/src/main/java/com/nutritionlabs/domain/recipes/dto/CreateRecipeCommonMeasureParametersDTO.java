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
public class CreateRecipeCommonMeasureParametersDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String singularName;
	private String pluralName;
	private Double quantity;
	private Double totalGrams;
	private Double ediblePortion;
}
