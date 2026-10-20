package com.nutritionlabs.dataaccess.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.MedicalHistoryEntity;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistoryEntity, Long> {

	Optional<MedicalHistoryEntity> findByUserEntity_Id(Long id);
}
