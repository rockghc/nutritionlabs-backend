package com.nutritionlabs.dataaccess.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.AppointmentInformationEntity;

public interface AppointmentInformationRepository extends JpaRepository<AppointmentInformationEntity, Long> {

	Optional<AppointmentInformationEntity> findByUserEntity_Id(Long id);
}
