package com.nutritionlabs.domain.recipes.dto;

import java.util.Optional;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RecipesPaginatedByUserParametersDTO {

	private String username;
	private Optional<String> searchName;
	private Optional<Long> category;
	private Pageable pageable;
}
