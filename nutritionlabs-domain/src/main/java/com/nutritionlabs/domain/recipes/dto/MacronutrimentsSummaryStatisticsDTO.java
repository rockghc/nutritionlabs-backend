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
public class MacronutrimentsSummaryStatisticsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private int energy;
	private int fat;
	private int carbohydrate;
	private int protein;
	private int fiber;
}
