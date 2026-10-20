package com.nutritionlabs.dataaccess.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.UserTagEntity;

public interface UserTagRepository extends JpaRepository<UserTagEntity, Long> {

	List<UserTagEntity> getDistinctByUserEntities_UserUserEntity_UserUserPkEntity_UsernameOrderByName(String username);

	List<UserTagEntity> getByUserEntities_IdAndUserEntities_UserUserEntity_UserUserPkEntity_UsernameOrderByName(Long id, String username);
}
