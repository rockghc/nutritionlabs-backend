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
public class NutritionalInformationDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String label;
	private String nutrimentsPer100;
	private String nutrimentsPer120;
	private String percentageValue;
}
