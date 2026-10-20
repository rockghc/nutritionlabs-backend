package com.nutritionlabs.dataaccess.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.DietaryHistoryAllergiesEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryAllergyPkEntity;

public interface DietaryHistoryAllergiesRepository extends JpaRepository<DietaryHistoryAllergiesEntity, DietaryHistoryAllergyPkEntity> {

	List<DietaryHistoryAllergiesEntity> findByDietaryHistory_UserEntity_Id(Long idUser);

	Optional<DietaryHistoryAllergiesEntity> findByDietaryHistory_IdAndAllergyEntity_Id(Long idDietaryHistory, Long idAllergyEntity);
}
