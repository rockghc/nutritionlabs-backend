package com.nutritionlabs.dataaccess.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.WorkplaceEntity;

public interface WorkplaceRepository extends JpaRepository<WorkplaceEntity, Long> {

	List<WorkplaceEntity> findByWorkplaceUserEntity_WorkplaceUserPkEntity_Username(String username);
}
