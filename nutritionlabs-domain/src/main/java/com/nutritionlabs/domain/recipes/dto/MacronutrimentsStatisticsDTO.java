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
public class MacronutrimentsStatisticsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long idRecipe;
	private MacronutrimentsSummaryStatisticsDTO macronutrimentsSummaryStatistics;
	private MacronutrimentsDistributionStatisticsDTO macronutrimentsDistributionStatistics;
}
