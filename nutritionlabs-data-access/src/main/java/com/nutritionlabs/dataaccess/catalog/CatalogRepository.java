package com.nutritionlabs.dataaccess.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.catalog.entity.CatalogEntity;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {
}
