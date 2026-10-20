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
public class CatalogLabelDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String label;
}
