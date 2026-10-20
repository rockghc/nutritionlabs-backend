package com.nutritionlabs.dataaccess.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.MedicalHistoryDiseasesEntity;
import com.nutritionlabs.domain.users.entity.MedicalHistoryDiseasePkEntity;

public interface MedicalHistoryDiseasesRepository extends JpaRepository<MedicalHistoryDiseasesEntity, MedicalHistoryDiseasePkEntity> {

	List<MedicalHistoryDiseasesEntity> findByMedicalHistory_UserEntity_Id(Long idUser);

	Optional<MedicalHistoryDiseasesEntity> findByMedicalHistory_IdAndDiseaseEntity_Id(Long idMedicalHistory, Long idDiseaseEntity);
}
