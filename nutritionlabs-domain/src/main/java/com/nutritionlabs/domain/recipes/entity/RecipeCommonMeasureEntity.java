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
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "recipeEntity")
@Builder
@Setter
@Getter
@Table(name = "nlabs_recipe_common_measures")
@Entity
public class RecipeCommonMeasureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_recipe")
	private RecipeEntity recipeEntity;

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
	private long version;

	public void updateCommonMeasure(String singularName, String pluralName, Double quantity, Double totalGrams, Double ediblePortion) {
		this.setSingularName(singularName);
		this.setPluralName(pluralName);
		this.setQuantity(quantity);
		this.setTotalGrams(totalGrams);
		this.setEdiblePortion(ediblePortion);
	}

	public RecipeCommonMeasureEntity clone() {
		return RecipeCommonMeasureEntity.builder()
				.singularName(getSingularName())
				.pluralName(getPluralName())
				.quantity(getQuantity())
				.totalGrams(getTotalGrams())
				.ediblePortion(getEdiblePortion())
				.build();
	}
}
