package com.nutritionlabs.domain.recipes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "nlabs_recipe_cooking_methods")
@Entity
public class RecipeCookingMethodEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_recipe")
	private RecipeEntity recipeEntity;

	@Column(name = "name")
	private String name;

	@Column(name = "order_items")
	private Long orderItems;

	@Version
	private long version;

	public RecipeCookingMethodEntity clone() {
		return RecipeCookingMethodEntity.builder()
				.name(getName())
				.orderItems(getOrderItems())
				.build();
	}
}
