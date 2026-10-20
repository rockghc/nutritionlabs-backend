package com.nutritionlabs.domain.food.dto;

import java.util.Optional;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class FoodDomainParametersDTO {

	private String username;
	private Optional<String> searchName;
	private Optional<Long> source;
	private String foodType;
	private boolean withCommonMeasures;
	private Pageable pageable;
}
