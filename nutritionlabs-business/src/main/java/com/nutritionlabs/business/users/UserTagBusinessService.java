package com.nutritionlabs.business.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.dataaccess.users.UserRepository;
import com.nutritionlabs.dataaccess.users.UserTagRepository;
import com.nutritionlabs.domain.users.dto.UserTagCreationParamsDTO;
import com.nutritionlabs.domain.users.entity.UserEntity;
import com.nutritionlabs.domain.users.entity.UserTagEntity;
import com.nutritionlabs.mappers.users.UsersMapper;
import com.nutritionlabs.properties.PropertiesReader;

/**
 * Tags of the patients.
 */
@Service
@Transactional(readOnly = true)
public class UserTagBusinessService {

	@Autowired
	private UserTagRepository userTagRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PropertiesReader propertiesReader;

	public List<UserTagEntity> getAllTags(String username, Optional<Long> idUser) {
		if (idUser.isPresent()) {
			return userTagRepository.getByUserEntities_IdAndUserEntities_UserUserEntity_UserUserPkEntity_UsernameOrderByName(idUser.get(), username);
		}
		return userTagRepository.getDistinctByUserEntities_UserUserEntity_UserUserPkEntity_UsernameOrderByName(username);
	}

	/**
	 * Adds a tag to a patient. An existing tag is used when the id is given, otherwise the tag is created.
	 */
	@Transactional
	public void createTag(String username, UserTagCreationParamsDTO tagCreationParamsDTO) {
		UserEntity userEntity = getUser(tagCreationParamsDTO.getIdUser());
		validateAccess(userEntity, username);
		UserTagEntity userTagEntity = tagCreationParamsDTO.getId() != null
				? userTagRepository.findById(tagCreationParamsDTO.getId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"))
				: userTagRepository.save(UsersMapper.MAPPER.tagParametersToUserTagEntity(tagCreationParamsDTO));
		if (userEntity.getUserTagsEntities() == null) {
			userEntity.setUserTagsEntities(new ArrayList<>());
		}
		if (!userEntity.getUserTagsEntities().contains(userTagEntity)) {
			userEntity.getUserTagsEntities().add(userTagEntity);
		}
		userRepository.save(userEntity);
	}

	/**
	 * Removes a tag from a patient. The tag is deleted when no other patient uses it.
	 */
	@Transactional
	public void deleteTag(String username, Long idTag, Long idUser) {
		UserTagEntity userTagEntity = userTagRepository.findById(idTag)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
		List<UserEntity> userEntities = userTagEntity.getUserEntities();
		if (userEntities == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, propertiesReader.getProperty("tag.userNotFound"));
		}
		UserEntity userEntity = getUser(idUser);
		validateAccess(userEntity, username);
		userEntity.getUserTagsEntities().remove(userTagEntity);
		if (userEntities.size() <= 1) {
			userRepository.flush();
			userTagRepository.delete(userTagEntity);
		}
	}

	private UserEntity getUser(Long idUser) {
		return userRepository.findById(idUser)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, propertiesReader.getProperty("tag.userNotFound")));
	}

	private void validateAccess(UserEntity userEntity, String username) {
		userEntity.getUserUserEntity()
				.stream()
				.filter(user -> user.getUserUserPkEntity().getUsername().equals(username))
				.findAny()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, propertiesReader.getProperty("tag.addTagDenied")));
	}
}
