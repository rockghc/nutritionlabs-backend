package com.nutritionlabs.domain.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "nlabs_foods_nutriments")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodNutrimentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_food")
	private FoodEntity foodEntity;

	@Column(name = "id_nutriment_catalog")
	private int idNutrimentCatalog;

	@Column(name = "quantity")
	private double quantity;

	@Version
	private long version;
}
