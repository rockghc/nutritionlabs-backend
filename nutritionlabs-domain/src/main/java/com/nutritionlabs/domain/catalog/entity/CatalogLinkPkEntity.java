package com.nutritionlabs.domain.catalog.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class CatalogLinkPkEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_catalog")
	private Long catalogDetail;

	@Column(name = "id_catalog_link")
	private Long catalogDetailLink;
}
