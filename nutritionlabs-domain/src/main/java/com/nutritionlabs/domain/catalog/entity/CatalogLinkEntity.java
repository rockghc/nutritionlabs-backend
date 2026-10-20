package com.nutritionlabs.domain.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "nlabs_catalog_link")
@Entity
public class CatalogLinkEntity {

	@EmbeddedId
	private CatalogLinkPkEntity catalogLinkPk;

	/**
	 * Detail referenced by {@code id_catalog}, the one that gives the value of the link.
	 */
	@MapsId("catalogDetail")
	@ManyToOne
	@JoinColumn(name = "id_catalog")
	private CatalogDetailEntity catalogDetail;

	/**
	 * Detail referenced by {@code id_catalog_link}, the owner of the link.
	 */
	@MapsId("catalogDetailLink")
	@ManyToOne
	@JoinColumn(name = "id_catalog_link")
	private CatalogDetailEntity linkedCatalogDetail;

	@Column(name = "link_type")
	private String linkType;
}
