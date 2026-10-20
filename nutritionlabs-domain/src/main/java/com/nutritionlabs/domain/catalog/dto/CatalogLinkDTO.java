package com.nutritionlabs.domain.catalog.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class CatalogLinkDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private CatalogDetailDTO catalogDetail;
	private String linkType;
}
