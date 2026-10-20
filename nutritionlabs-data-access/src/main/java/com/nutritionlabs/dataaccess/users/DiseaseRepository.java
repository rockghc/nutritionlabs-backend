package com.nutritionlabs.dataaccess.users;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.DiseaseEntity;

public interface DiseaseRepository extends JpaRepository<DiseaseEntity, Long> {
}
