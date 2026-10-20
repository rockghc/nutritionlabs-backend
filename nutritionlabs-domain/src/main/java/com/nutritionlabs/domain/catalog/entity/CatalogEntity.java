package com.nutritionlabs.domain.catalog.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "nlabs_catalogs")
@Entity
public class CatalogEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "label")
	private String label;

	@Column(name = "catalog_type")
	private String catalogType;

	@OneToMany(mappedBy = "catalog")
	private List<CatalogDetailEntity> catalogDetails;
}
