package com.nutritionlabs.domain.food.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "foodEntity")
@Setter
@Getter
@Table(name = "nlabs_food_common_measures")
@Entity
public class FoodCommonMeasureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_food")
	private FoodEntity foodEntity;

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
}
