package com.nutritionlabs.dataaccess.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.UserProfileEntity;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

	boolean existsByUsername(String username);

	Optional<UserProfileEntity> findByUsername(String username);
}
