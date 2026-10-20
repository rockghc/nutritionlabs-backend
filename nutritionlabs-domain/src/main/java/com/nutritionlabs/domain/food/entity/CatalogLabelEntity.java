package com.nutritionlabs.domain.food.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Label of the source of a food.
 */
@Setter
@Getter
@Entity
@Table(name = "nlabs_catalogs_labels")
public class CatalogLabelEntity {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "label")
	private String label;
}
