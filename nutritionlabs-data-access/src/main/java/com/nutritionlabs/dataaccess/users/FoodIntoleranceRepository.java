package com.nutritionlabs.dataaccess.users;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.FoodIntoleranceEntity;

public interface FoodIntoleranceRepository extends JpaRepository<FoodIntoleranceEntity, Long> {
}
