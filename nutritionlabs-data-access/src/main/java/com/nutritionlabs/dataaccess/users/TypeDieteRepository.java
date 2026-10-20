package com.nutritionlabs.dataaccess.users;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.TypeDieteEntity;

public interface TypeDieteRepository extends JpaRepository<TypeDieteEntity, Long> {
}
