package com.nutritionlabs.dataaccess.food;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.food.entity.FoodEntity;

public interface FoodRepository extends JpaRepository<FoodEntity, Long>, FoodCustomRepository {
}
