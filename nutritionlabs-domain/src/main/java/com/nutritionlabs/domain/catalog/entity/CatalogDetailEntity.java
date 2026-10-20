package com.nutritionlabs.domain.catalog.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "nlabs_catalog_details")
@Entity
public class CatalogDetailEntity {

	@Id
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_catalog")
	private CatalogEntity catalog;

	@Column(name = "name")
	private String name;

	@Column(name = "label")
	private String label;

	/**
	 * Links whose target ({@code id_catalog_link}) is this detail.
	 */
	@OneToMany(mappedBy = "linkedCatalogDetail")
	private List<CatalogLinkEntity> catalogLinks;
}
