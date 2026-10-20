package com.nutritionlabs.business.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.dataaccess.users.AppointmentInformationRepository;
import com.nutritionlabs.dataaccess.users.DietaryHistoryRepository;
import com.nutritionlabs.dataaccess.users.MedicalHistoryRepository;
import com.nutritionlabs.dataaccess.users.PersonalHistoryRepository;
import com.nutritionlabs.dataaccess.users.UserRepository;
import com.nutritionlabs.domain.users.entity.AppointmentInformationEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryEntity;
import com.nutritionlabs.domain.users.entity.MedicalHistoryEntity;
import com.nutritionlabs.domain.users.entity.PersonalHistoryEntity;
import com.nutritionlabs.domain.users.entity.UserEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;

/**
 * Gets the clinical histories of a patient, creating the empty history when the patient does not have it.
 */
@Component
@Transactional
public class UserHistoriesFactory {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AppointmentInformationRepository appointmentInformationRepository;

	@Autowired
	private PersonalHistoryRepository personalHistoryRepository;

	@Autowired
	private DietaryHistoryRepository dietaryHistoryRepository;

	@Autowired
	private MedicalHistoryRepository medicalHistoryRepository;

	public void createHistories(UserEntity userEntity) {
		getOrCreateAppointmentInformation(userEntity.getId());
		getOrCreatePersonalHistory(userEntity.getId());
		getOrCreateDietaryHistory(userEntity.getId());
		getOrCreateMedicalHistory(userEntity.getId());
	}

	public AppointmentInformationEntity getOrCreateAppointmentInformation(Long idUser) {
		return appointmentInformationRepository.findByUserEntity_Id(idUser)
				.orElseGet(() -> appointmentInformationRepository.save(AppointmentInformationEntity.builder().userEntity(getUser(idUser)).build()));
	}

	public PersonalHistoryEntity getOrCreatePersonalHistory(Long idUser) {
		return personalHistoryRepository.findByUserEntity_Id(idUser)
				.orElseGet(() -> personalHistoryRepository.save(PersonalHistoryEntity.builder().userEntity(getUser(idUser)).build()));
	}

	public DietaryHistoryEntity getOrCreateDietaryHistory(Long idUser) {
		return dietaryHistoryRepository.findByUserEntity_Id(idUser)
				.orElseGet(() -> dietaryHistoryRepository.save(DietaryHistoryEntity.builder().userEntity(getUser(idUser)).build()));
	}

	public MedicalHistoryEntity getOrCreateMedicalHistory(Long idUser) {
		return medicalHistoryRepository.findByUserEntity_Id(idUser)
				.orElseGet(() -> medicalHistoryRepository.save(MedicalHistoryEntity.builder().userEntity(getUser(idUser)).build()));
	}

	private UserEntity getUser(Long idUser) {
		return userRepository.findById(idUser)
				.orElseThrow(() -> new EntityNotFoundException(UserEntity.class, "id", String.valueOf(idUser)));
	}
}
