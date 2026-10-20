package com.nutritionlabs.domain.recipes.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Table(name = "nlabs_recipe_ingredients")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_recipe")
	@JsonIgnore
	private RecipeEntity recipeEntity;

	@Builder.Default
	@OneToMany(mappedBy = "recipeIngredientEntity", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	private List<IngredientFoodEntity> ingredientFoodEntities = new ArrayList<>();

	@Version
	private long version;

	/**
	 * The ingredient foods are alternatives, so the weight of the ingredient is their average weight.
	 */
	public double getTotalGrams() {
		return ingredientFoodEntities
				.stream()
				.mapToDouble(IngredientFoodEntity::getTotalGrams)
				.average()
				.orElse(0);
	}
}
