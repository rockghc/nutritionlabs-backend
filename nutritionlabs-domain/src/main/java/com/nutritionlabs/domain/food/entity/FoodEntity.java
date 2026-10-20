package com.nutritionlabs.domain.food.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "nlabs_foods")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "food_type", discriminatorType = DiscriminatorType.STRING)
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("FOOD")
public class FoodEntity implements Serializable {

	private static final long serialVersionUID = -2198443454779459167L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "food_group")
	private Long foodGroup;

	@Column(name = "quantity")
	private Long quantity;

	@ManyToOne
	@JoinColumn(name = "id_source")
	private CatalogLabelEntity source;

	@Version
	private long version;

	@OneToMany(mappedBy = "foodEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<FoodNutrimentEntity> nutriments;

	@OneToMany(mappedBy = "foodEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<FoodCommonMeasureEntity> commonMeasures;

	@JsonIgnore
	@OneToMany(mappedBy = "foodEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	private List<FoodUser> foodUsers;
}
