package com.nutritionlabs.business.users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.business.common.ValueModificatorService;
import com.nutritionlabs.dataaccess.users.UserRepository;
import com.nutritionlabs.dataaccess.users.WorkplaceRepository;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.users.dto.UserDTO;
import com.nutritionlabs.domain.users.dto.WorkplaceDTO;
import com.nutritionlabs.domain.users.entity.UserEntity;
import com.nutritionlabs.domain.users.entity.UserPkEntity;
import com.nutritionlabs.domain.users.entity.UserUserEntity;
import com.nutritionlabs.domain.users.entity.WorkplaceEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.users.UsersMapper;

/**
 * Patients ("users") managed by the Keycloak user ("username") and the workplaces of the Keycloak user.
 */
@Service
@Transactional
public class UserBusinessService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WorkplaceRepository workplaceRepository;

	@Autowired
	private ValueModificatorService valueModificatorService;

	@Autowired
	private UserHistoriesFactory userHistoriesFactory;

	@Transactional(readOnly = true)
	public UserDTO getUserEntity(Long id) {
		return UsersMapper.MAPPER.userEntityToDTO(getUser(id));
	}

	@Transactional(readOnly = true)
	public List<UserDTO> getUsersEntities(String username) {
		return userRepository.findByUserUserEntity_UserUserPkEntity_Username(username)
				.stream()
				.map(UsersMapper.MAPPER::userEntityToDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<WorkplaceDTO> getWorkplacesEntities(String username) {
		return workplaceRepository.findByWorkplaceUserEntity_WorkplaceUserPkEntity_Username(username)
				.stream()
				.map(UsersMapper.MAPPER::workplaceEntityToDTO)
				.collect(Collectors.toList());
	}

	/**
	 * Moves a patient of the Keycloak user to another workplace of the Keycloak user.
	 */
	public void updateWorkplace(Long userId, Long workplaceId, String username) {
		WorkplaceEntity workplaceEntity = workplaceRepository.findById(workplaceId)
				.filter(workplace -> workplace.getWorkplaceUserEntity()
						.stream()
						.anyMatch(user -> user.getWorkplaceUserPkEntity().getUsername().equals(username)))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Workplace not available for the user"));
		UserEntity userEntity = userRepository.findById(userId)
				.filter(user -> isManagedBy(user, username))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient not available for the user"));
		userEntity.setWorkplace(workplaceEntity);
	}

	/**
	 * Creates a patient managed by the Keycloak user, with its empty clinical histories.
	 */
	public UserDTO createUser(UserEntity userEntity, String username) {
		userEntity.setId(null);
		UserPkEntity userPkEntity = UserPkEntity.builder().username(username).build();
		UserUserEntity userUserEntity = UserUserEntity.builder().userUserPkEntity(userPkEntity).userEntity(userEntity).build();
		userEntity.setUserUserEntity(new ArrayList<>(List.of(userUserEntity)));
		if (userEntity.getWorkplace() != null && userEntity.getWorkplace().getId() != null) {
			userEntity.setWorkplace(workplaceRepository.getReferenceById(userEntity.getWorkplace().getId()));
		} else {
			userEntity.setWorkplace(null);
		}
		userEntity.setCreationDate(LocalDateTime.now());
		UserEntity userSaved = userRepository.save(userEntity);
		userHistoriesFactory.createHistories(userSaved);
		return UsersMapper.MAPPER.userEntityToDTO(userSaved);
	}

	public UserDTO updateUser(SingleValueDTO<?> payload) {
		UserEntity userEntity = getUser(payload.getId());
		valueModificatorService.modifyValue(userEntity, payload);
		userRepository.saveAndFlush(userEntity);
		return UsersMapper.MAPPER.userEntityToDTO(userEntity);
	}

	private UserEntity getUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(UserEntity.class, "id", String.valueOf(id)));
	}

	private boolean isManagedBy(UserEntity userEntity, String username) {
		return userEntity.getUserUserEntity()
				.stream()
				.anyMatch(user -> user.getUserUserPkEntity().getUsername().equals(username));
	}
}
