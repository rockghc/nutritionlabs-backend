package com.nutritionlabs.business.users;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.business.common.ValueModificatorService;
import com.nutritionlabs.dataaccess.users.DiseaseRepository;
import com.nutritionlabs.dataaccess.users.MedicalHistoryDiseasesRepository;
import com.nutritionlabs.dataaccess.users.MedicalHistoryRepository;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.users.dto.DiseaseDTO;
import com.nutritionlabs.domain.users.dto.MedicalHistoryDTO;
import com.nutritionlabs.domain.users.entity.DiseaseEntity;
import com.nutritionlabs.domain.users.entity.MedicalHistoryDiseasePkEntity;
import com.nutritionlabs.domain.users.entity.MedicalHistoryDiseasesEntity;
import com.nutritionlabs.domain.users.entity.MedicalHistoryEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.users.UsersMapper;

@Service
@Transactional(readOnly = true)
public class MedicalHistoryBusinessService {

	@Autowired
	private MedicalHistoryRepository medicalHistoryRepository;

	@Autowired
	private DiseaseRepository diseaseRepository;

	@Autowired
	private MedicalHistoryDiseasesRepository medicalHistoryDiseasesRepository;

	@Autowired
	private ValueModificatorService valueModificatorService;

	@Autowired
	private UserHistoriesFactory userHistoriesFactory;

	public Optional<MedicalHistoryDTO> getMedicalHistory(Long idUser) {
		return medicalHistoryRepository.findByUserEntity_Id(idUser)
				.map(UsersMapper.MAPPER::medicalHistoryToDTO);
	}

	@Transactional
	public MedicalHistoryDTO updateMedicalHistory(SingleValueDTO<?> payload) {
		MedicalHistoryEntity medicalHistory = medicalHistoryRepository.findById(payload.getId())
				.orElseThrow(() -> new EntityNotFoundException(MedicalHistoryEntity.class, "id", String.valueOf(payload.getId())));
		valueModificatorService.modifyValue(medicalHistory, payload);
		medicalHistoryRepository.saveAndFlush(medicalHistory);
		return UsersMapper.MAPPER.medicalHistoryToDTO(medicalHistory);
	}

	public List<DiseaseDTO> getDiseases(Long idUser) {
		return medicalHistoryDiseasesRepository.findByMedicalHistory_UserEntity_Id(idUser)
				.stream()
				.map(MedicalHistoryDiseasesEntity::getDiseaseEntity)
				.map(UsersMapper.MAPPER::diseaseToDTO)
				.collect(Collectors.toList());
	}

	public List<DiseaseDTO> getAllDiseases() {
		return diseaseRepository.findAll()
				.stream()
				.map(UsersMapper.MAPPER::diseaseToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public DiseaseDTO setDisease(Long idUser, Long idDisease) {
		MedicalHistoryEntity medicalHistory = userHistoriesFactory.getOrCreateMedicalHistory(idUser);
		DiseaseEntity disease = diseaseRepository.findById(idDisease)
				.orElseThrow(() -> new EntityNotFoundException(DiseaseEntity.class, "id", String.valueOf(idDisease)));
		MedicalHistoryDiseasesEntity medicalHistoryDiseasesEntity = MedicalHistoryDiseasesEntity.builder()
				.medicalHistoryDiseasePkEntity(MedicalHistoryDiseasePkEntity.builder()
						.idMedicalHistory(medicalHistory.getId())
						.idDisease(disease.getId())
						.build())
				.medicalHistory(medicalHistory)
				.diseaseEntity(disease)
				.build();
		return UsersMapper.MAPPER.diseaseToDTO(medicalHistoryDiseasesRepository.save(medicalHistoryDiseasesEntity).getDiseaseEntity());
	}

	@Transactional
	public void deleteDisease(Long idUser, Long idDisease) {
		medicalHistoryRepository.findByUserEntity_Id(idUser)
				.flatMap(medicalHistory -> medicalHistoryDiseasesRepository.findByMedicalHistory_IdAndDiseaseEntity_Id(medicalHistory.getId(), idDisease))
				.ifPresent(medicalHistoryDiseasesRepository::delete);
	}
}
