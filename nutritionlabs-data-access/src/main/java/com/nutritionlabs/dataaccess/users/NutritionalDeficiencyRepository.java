package com.nutritionlabs.dataaccess.users;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.NutritionalDeficiencyEntity;

public interface NutritionalDeficiencyRepository extends JpaRepository<NutritionalDeficiencyEntity, Long> {
}
