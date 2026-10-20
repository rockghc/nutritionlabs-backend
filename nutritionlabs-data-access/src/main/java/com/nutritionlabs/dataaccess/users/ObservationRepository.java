package com.nutritionlabs.dataaccess.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.ObservationEntity;

public interface ObservationRepository extends JpaRepository<ObservationEntity, Long> {

	Page<ObservationEntity> findByUserEntity_IdOrderByIdDesc(Long id, Pageable pageable);
}
