package com.nutritionlabs.dataaccess.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.PersonalHistoryEntity;

public interface PersonalHistoryRepository extends JpaRepository<PersonalHistoryEntity, Long> {

	Optional<PersonalHistoryEntity> findByUserEntity_Id(Long id);
}
