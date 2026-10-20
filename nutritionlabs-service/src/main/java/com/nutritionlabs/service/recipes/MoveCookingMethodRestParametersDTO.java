package com.nutritionlabs.service.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * New position of a cooking method.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoveCookingMethodRestParametersDTO {

	private Integer orderItem;
}
