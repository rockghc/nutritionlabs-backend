package com.nutritionlabs.domain.food.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPPLEMENT")
public class SupplementEntity extends FoodEntity {

	private static final long serialVersionUID = 1L;
}
