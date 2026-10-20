package com.nutritionlabs.service.food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Filters of the food list.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRestServiceParametersDTO {

	private String searchName;
	private Long source;
	private Boolean withCommonMeasures;
}
