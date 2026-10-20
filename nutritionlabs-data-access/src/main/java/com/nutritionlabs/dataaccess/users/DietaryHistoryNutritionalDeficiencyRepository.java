package com.nutritionlabs.dataaccess.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.DietaryHistoryNutritionalDeficiencyEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryNutritionalDeficiencyPkEntity;

public interface DietaryHistoryNutritionalDeficiencyRepository extends JpaRepository<DietaryHistoryNutritionalDeficiencyEntity, DietaryHistoryNutritionalDeficiencyPkEntity> {

	List<DietaryHistoryNutritionalDeficiencyEntity> findByDietaryHistory_UserEntity_Id(Long idUser);

	Optional<DietaryHistoryNutritionalDeficiencyEntity> findByDietaryHistory_IdAndNutritionalDeficiencyEntity_Id(Long idDietaryHistory, Long idNutritionalDeficiencyEntity);
}
