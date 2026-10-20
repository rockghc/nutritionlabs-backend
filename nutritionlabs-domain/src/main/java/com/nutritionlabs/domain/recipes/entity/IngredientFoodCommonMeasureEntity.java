package com.nutritionlabs.domain.recipes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "ingredientFoodEntity")
@Setter
@Getter
@Builder
@Table(name = "nlabs_ingredient_foods_common_mesure")
@Entity
public class IngredientFoodCommonMeasureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Id of the food common measure this one was copied from.
	 */
	@Column(name = "id_external")
	private Long idExternal;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_ingredient_food")
	private IngredientFoodEntity ingredientFoodEntity;

	@Column(name = "singular_name")
	private String singularName;

	@Column(name = "plural_name")
	private String pluralName;

	@Column(name = "quantity")
	private Double quantity;

	@Column(name = "total_grams")
	private Double totalGrams;

	@Column(name = "edible_portion")
	private Double ediblePortion;

	@Version
	private Long version;

	/**
	 * Grams of one unit of the common measure.
	 */
	public Double getTotalGrams() {
		if (totalGrams == null || quantity == null) {
			return 0d;
		}
		return totalGrams / quantity;
	}

	public IngredientFoodCommonMeasureEntity clone() {
		return IngredientFoodCommonMeasureEntity.builder()
				.idExternal(this.idExternal)
				.singularName(this.singularName)
				.pluralName(this.pluralName)
				.quantity(this.quantity)
				.totalGrams(this.totalGrams)
				.ediblePortion(this.ediblePortion)
				.build();
	}
}
