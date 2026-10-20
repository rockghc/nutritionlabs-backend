package com.nutritionlabs.dataaccess.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.catalog.entity.CatalogDetailEntity;

public interface CatalogDetailRepository extends JpaRepository<CatalogDetailEntity, Long> {
}
