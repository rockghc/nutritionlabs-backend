package com.nutritionlabs.dataaccess.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.DietaryHistoryEntity;

public interface DietaryHistoryRepository extends JpaRepository<DietaryHistoryEntity, Long> {

	Optional<DietaryHistoryEntity> findByUserEntity_Id(Long id);
}
