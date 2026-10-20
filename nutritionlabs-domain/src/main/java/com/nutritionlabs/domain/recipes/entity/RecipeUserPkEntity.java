package com.nutritionlabs.domain.recipes.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Setter
@Getter
@Embeddable
public class RecipeUserPkEntity implements Serializable {

	private static final long serialVersionUID = 8439652083957850795L;

	@JsonIgnore
	private Long idRecipe;

	private String username;
}
