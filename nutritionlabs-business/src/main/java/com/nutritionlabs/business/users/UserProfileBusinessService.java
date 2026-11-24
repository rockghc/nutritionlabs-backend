package com.nutritionlabs.business.users;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.dataaccess.users.UserProfileRepository;
import com.nutritionlabs.domain.users.dto.UserProfileDTO;
import com.nutritionlabs.domain.users.dto.UserProfileParamsDTO;
import com.nutritionlabs.domain.users.dto.WorkProfileParamsDTO;
import com.nutritionlabs.domain.users.entity.UserProfileEntity;

/**
 * Additional information of the accounts (Keycloak users), table nlabs_user_profiles.
 */
@Service
@Transactional(readOnly = true)
public class UserProfileBusinessService {

	private static final Pattern PHONE_PREFIX = Pattern.compile("^\\+[0-9]{1,4}$");

	private static final int WORKPLACE_NAME_MAX_LENGTH = 150;

	@Autowired
	private UserProfileRepository userProfileRepository;

	/**
	 * Keycloak stores the usernames in lower case, so the profile is stored and searched the same way.
	 */
	public static String normalizeUsername(String username) {
		return username.trim().toLowerCase(Locale.ROOT);
	}

	public boolean existsProfile(String username) {
		return userProfileRepository.existsByUsername(normalizeUsername(username));
	}

	/**
	 * Validates the optional profile data before the Keycloak user is created, so an invalid value does not leave a
	 * Keycloak user without profile.
	 */
	public void validate(UserProfileParamsDTO params) {
		if (params == null) {
			return;
		}
		if (params.getPhonePrefix() != null && !PHONE_PREFIX.matcher(params.getPhonePrefix()).matches()) {
			throw new IllegalArgumentException("The phone prefix must be like +34");
		}
		if (params.getMobileNumber() != null && params.getMobileNumber().length() > 20) {
			throw new IllegalArgumentException("The mobile number must have at most 20 characters");
		}
		if (params.getBirthdate() != null && params.getBirthdate().isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("The birthdate cannot be in the future");
		}
	}

	@Transactional
	public UserProfileDTO createProfile(String username, String idKeycloak, UserProfileParamsDTO params) {
		UserProfileParamsDTO profile = params == null ? new UserProfileParamsDTO() : params;
		UserProfileEntity entity = userProfileRepository.saveAndFlush(UserProfileEntity.builder()
				.username(normalizeUsername(username))
				.idKeycloak(idKeycloak)
				.idGender(profile.getIdGender())
				.idCountry(profile.getIdCountry())
				.birthdate(profile.getBirthdate())
				.phonePrefix(profile.getPhonePrefix())
				.mobileNumber(profile.getMobileNumber())
				.idProfession(profile.getIdProfession())
				.build());
		return toDTO(entity);
	}

	public UserProfileDTO getProfile(String username) {
		return toDTO(findProfile(username));
	}

	/**
	 * Stores the data of the "work-profile" interface. The values replace the current ones, so a null value clears
	 * it and the practice areas sent replace the stored ones.
	 */
	@Transactional
	public UserProfileDTO updateWorkProfile(String username, WorkProfileParamsDTO params) {
		WorkProfileParamsDTO work = params == null ? new WorkProfileParamsDTO() : params;
		String workplaceName = StringUtils.hasText(work.getWorkplaceName()) ? work.getWorkplaceName().trim() : null;
		if (workplaceName != null && workplaceName.length() > WORKPLACE_NAME_MAX_LENGTH) {
			throw new IllegalArgumentException(
					"The workplace name must have at most " + WORKPLACE_NAME_MAX_LENGTH + " characters");
		}
		Set<Long> practiceAreaIds = work.getPracticeAreaIds() == null ? Set.of() : work.getPracticeAreaIds();
		if (practiceAreaIds.contains(null)) {
			throw new IllegalArgumentException("The practice areas cannot contain empty values");
		}

		UserProfileEntity entity = findProfile(username);
		entity.setIdFeature(work.getIdFeature());
		entity.setWorkplaceName(workplaceName);
		entity.setIdClientsPerMonth(work.getIdClientsPerMonth());
		entity.getPracticeAreaIds().clear();
		entity.getPracticeAreaIds().addAll(practiceAreaIds);
		return toDTO(userProfileRepository.saveAndFlush(entity));
	}

	private UserProfileEntity findProfile(String username) {
		return userProfileRepository.findByUsername(normalizeUsername(username))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The user has no profile"));
	}

	private UserProfileDTO toDTO(UserProfileEntity entity) {
		return UserProfileDTO.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.idGender(entity.getIdGender())
				.idCountry(entity.getIdCountry())
				.birthdate(entity.getBirthdate())
				.phonePrefix(entity.getPhonePrefix())
				.mobileNumber(entity.getMobileNumber())
				.idProfession(entity.getIdProfession())
				.idFeature(entity.getIdFeature())
				.workplaceName(entity.getWorkplaceName())
				.practiceAreaIds(new TreeSet<>(entity.getPracticeAreaIds()))
				.idClientsPerMonth(entity.getIdClientsPerMonth())
				.creationDate(entity.getCreationDate())
				.updateDate(entity.getUpdateDate())
				.build();
	}
}
