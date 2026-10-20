package com.nutritionlabs.domain.recipes.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Copy of a food used as ingredient of a recipe, with its nutriments and common measures.
 */
@Setter
@Getter
@Table(name = "nlabs_ingredient_foods")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class IngredientFoodEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "real_name")
	private String realName;

	@Column(name = "quantity")
	private Double quantity;

	@Column(name = "id_food")
	private Long idFood;

	@ManyToOne
	@JoinColumn(name = "id_recipe_ingredients")
	@JsonIgnore
	private RecipeIngredientEntity recipeIngredientEntity;

	@ManyToOne
	@JoinColumn(name = "id_common_measure")
	private IngredientFoodCommonMeasureEntity ingredientCommonMeasureEntity;

	@OneToMany(mappedBy = "ingredientFoodEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<IngredientFoodNutrimentEntity> nutriments = new ArrayList<>();

	@OneToMany(mappedBy = "ingredientFoodEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<IngredientFoodCommonMeasureEntity> commonMeasures = new ArrayList<>();

	@Version
	private Long version;

	public double getTotalGrams() {
		double grams = getQuantity();
		if (getIngredientCommonMeasureEntity() != null) {
			return grams * getIngredientCommonMeasureEntity().getTotalGrams();
		}
		return grams;
	}

	/**
	 * Copies the ingredient food with its nutriments and common measures. The selected common measure of the
	 * copy is the copy of the selected common measure.
	 */
	public IngredientFoodEntity clone() {
		IngredientFoodEntity ingredientFoodEntity = new IngredientFoodEntity();
		ingredientFoodEntity.setName(this.name);
		ingredientFoodEntity.setRealName(this.realName);
		ingredientFoodEntity.setQuantity(this.quantity);
		ingredientFoodEntity.setIdFood(this.idFood);
		ingredientFoodEntity.setNutriments(getNutriments().stream()
				.map(IngredientFoodNutrimentEntity::clone)
				.peek(nutriment -> nutriment.setIngredientFoodEntity(ingredientFoodEntity))
				.collect(Collectors.toCollection(ArrayList::new)));
		List<IngredientFoodCommonMeasureEntity> commonMeasuresCloned = new ArrayList<>();
		for (IngredientFoodCommonMeasureEntity commonMeasure : getCommonMeasures()) {
			IngredientFoodCommonMeasureEntity commonMeasureCloned = commonMeasure.clone();
			commonMeasureCloned.setIngredientFoodEntity(ingredientFoodEntity);
			commonMeasuresCloned.add(commonMeasureCloned);
			if (isSelectedCommonMeasure(commonMeasure)) {
				ingredientFoodEntity.setIngredientCommonMeasureEntity(commonMeasureCloned);
			}
		}
		if (this.ingredientCommonMeasureEntity != null && ingredientFoodEntity.getIngredientCommonMeasureEntity() == null) {
			IngredientFoodCommonMeasureEntity selectedCommonMeasureCloned = this.ingredientCommonMeasureEntity.clone();
			selectedCommonMeasureCloned.setIngredientFoodEntity(ingredientFoodEntity);
			commonMeasuresCloned.add(selectedCommonMeasureCloned);
			ingredientFoodEntity.setIngredientCommonMeasureEntity(selectedCommonMeasureCloned);
		}
		ingredientFoodEntity.setCommonMeasures(commonMeasuresCloned);
		return ingredientFoodEntity;
	}

	private boolean isSelectedCommonMeasure(IngredientFoodCommonMeasureEntity commonMeasure) {
		IngredientFoodCommonMeasureEntity selected = this.ingredientCommonMeasureEntity;
		return selected != null
				&& (commonMeasure == selected || (commonMeasure.getId() != null && commonMeasure.getId().equals(selected.getId())));
	}
}
