package com.nutritionlabs.dataaccess.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.DietaryHistoryTypesDieteEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryTypeDietePkEntity;

public interface DietaryHistoryTypesDieteRepository extends JpaRepository<DietaryHistoryTypesDieteEntity, DietaryHistoryTypeDietePkEntity> {

	List<DietaryHistoryTypesDieteEntity> findByDietaryHistory_UserEntity_Id(Long idUser);

	Optional<DietaryHistoryTypesDieteEntity> findByDietaryHistory_IdAndTypeDiete_Id(Long idDietaryHistory, Long idTypeDiete);
}
