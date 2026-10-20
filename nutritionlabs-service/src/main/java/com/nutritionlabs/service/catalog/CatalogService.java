package com.nutritionlabs.service.catalog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.catalog.CatalogBusinessService;
import com.nutritionlabs.domain.catalog.dto.CatalogDTO;
import com.nutritionlabs.domain.catalog.dto.CatalogDetailDTO;
import com.nutritionlabs.service.ResourcePaths;

/**
 * Catalogs (nutriments, units, sources...).
 */
@RestController
@RequestMapping(ResourcePaths.CATALOG + "/catalog")
public class CatalogService {

	@Autowired
	private CatalogBusinessService catalogBusinessService;

	@GetMapping
	public List<CatalogDTO> getCatalogEntities() {
		return catalogBusinessService.getCatalogs();
	}

	@GetMapping("/{id}")
	public CatalogDTO getCatalogEntity(@PathVariable Long id) {
		return catalogBusinessService.getCatalog(id);
	}

	@GetMapping("/{id}/catalogdetail/{idCatalogDetail}")
	public CatalogDetailDTO getCatalogDetailEntity(@PathVariable Long id, @PathVariable Long idCatalogDetail) {
		return catalogBusinessService.getCatalogDetail(idCatalogDetail);
	}
}
