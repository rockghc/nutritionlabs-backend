package com.nutritionlabs.domain.food.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodNutrimentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idNutrimentCatalog;
	private double quantity;
}
