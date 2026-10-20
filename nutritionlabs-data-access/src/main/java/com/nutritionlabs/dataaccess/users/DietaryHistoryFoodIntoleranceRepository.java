package com.nutritionlabs.dataaccess.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.DietaryHistoryFoodIntoleranceEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryFoodIntolerancePkEntity;

public interface DietaryHistoryFoodIntoleranceRepository extends JpaRepository<DietaryHistoryFoodIntoleranceEntity, DietaryHistoryFoodIntolerancePkEntity> {

	List<DietaryHistoryFoodIntoleranceEntity> findByDietaryHistory_UserEntity_Id(Long idUser);

	Optional<DietaryHistoryFoodIntoleranceEntity> findByDietaryHistory_IdAndFoodIntoleranceEntity_Id(Long idDietaryHistory, Long idFoodIntoleranceEntity);
}
