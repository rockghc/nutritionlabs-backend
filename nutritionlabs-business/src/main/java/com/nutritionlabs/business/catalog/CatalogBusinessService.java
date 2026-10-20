package com.nutritionlabs.business.catalog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.dataaccess.catalog.CatalogDetailRepository;
import com.nutritionlabs.dataaccess.catalog.CatalogRepository;
import com.nutritionlabs.domain.catalog.dto.CatalogDTO;
import com.nutritionlabs.domain.catalog.dto.CatalogDetailDTO;
import com.nutritionlabs.domain.catalog.entity.CatalogDetailEntity;
import com.nutritionlabs.domain.catalog.entity.CatalogEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.catalog.CatalogMapper;

@Service
@Transactional(readOnly = true)
public class CatalogBusinessService {

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private CatalogDetailRepository catalogDetailRepository;

	public List<CatalogDTO> getCatalogs() {
		return CatalogMapper.MAPPER.catalogEntitiesToDTOs(catalogRepository.findAll());
	}

	public CatalogDTO getCatalog(Long id) {
		return catalogRepository.findById(id)
				.map(CatalogMapper.MAPPER::catalogEntityToDTO)
				.orElseThrow(() -> new EntityNotFoundException(CatalogEntity.class, "id", String.valueOf(id)));
	}

	public CatalogDetailDTO getCatalogDetail(Long id) {
		return catalogDetailRepository.findById(id)
				.map(CatalogMapper.MAPPER::catalogDetailEntityToDTO)
				.orElseThrow(() -> new EntityNotFoundException(CatalogDetailEntity.class, "id", String.valueOf(id)));
	}
}
