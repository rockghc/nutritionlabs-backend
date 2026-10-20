package com.nutritionlabs.domain.recipes.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "recipeEntity")
@Setter
@Getter
@Table(name = "nlabs_recipe_users")
@Entity
@Builder
public class RecipeUserEntity {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "idRecipe", column = @Column(name = "id_recipe")),
		@AttributeOverride(name = "username", column = @Column(name = "username"))
	})
	private RecipeUserPkEntity recipeUserPkEntity;

	@ManyToOne
	@JoinColumn(name = "id_recipe")
	@MapsId("idRecipe")
	private RecipeEntity recipeEntity;

	@Version
	private long version;
}
