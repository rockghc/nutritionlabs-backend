package com.nutritionlabs.dataaccess.users;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.ClinicalGoalEntity;

public interface ClinicalGoalRepository extends JpaRepository<ClinicalGoalEntity, Long> {
}
