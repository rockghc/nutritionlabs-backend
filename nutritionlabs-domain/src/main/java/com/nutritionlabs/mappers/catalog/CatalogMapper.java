package com.nutritionlabs.mappers.catalog;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.catalog.dto.CatalogDTO;
import com.nutritionlabs.domain.catalog.dto.CatalogDetailDTO;
import com.nutritionlabs.domain.catalog.dto.CatalogLinkDTO;
import com.nutritionlabs.domain.catalog.entity.CatalogDetailEntity;
import com.nutritionlabs.domain.catalog.entity.CatalogEntity;
import com.nutritionlabs.domain.catalog.entity.CatalogLinkEntity;

@Mapper
public interface CatalogMapper {

	public CatalogMapper MAPPER = Mappers.getMapper(CatalogMapper.class);

	CatalogDTO catalogEntityToDTO(CatalogEntity catalogEntity);

	List<CatalogDTO> catalogEntitiesToDTOs(List<CatalogEntity> catalogEntities);

	CatalogDetailDTO catalogDetailEntityToDTO(CatalogDetailEntity catalogDetailEntity);

	CatalogLinkDTO catalogLinkEntityToDTO(CatalogLinkEntity catalogLinkEntity);
}
