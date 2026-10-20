package com.nutritionlabs.dataaccess.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	List<UserEntity> findByUserUserEntity_UserUserPkEntity_Username(String username);
}
