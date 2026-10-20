package com.nutritionlabs.mappers.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nutritionlabs.domain.users.dto.AllergyDTO;
import com.nutritionlabs.domain.users.dto.AppointmentInformationDTO;
import com.nutritionlabs.domain.users.dto.ClinicalGoalDTO;
import com.nutritionlabs.domain.users.dto.DietaryHistoryDTO;
import com.nutritionlabs.domain.users.dto.DiseaseDTO;
import com.nutritionlabs.domain.users.dto.FoodIntoleranceDTO;
import com.nutritionlabs.domain.users.dto.MedicalHistoryDTO;
import com.nutritionlabs.domain.users.dto.NutritionalDeficiencyDTO;
import com.nutritionlabs.domain.users.dto.ObservationDTO;
import com.nutritionlabs.domain.users.dto.PersonalHistoryDTO;
import com.nutritionlabs.domain.users.dto.TypeDieteDTO;
import com.nutritionlabs.domain.users.dto.UserDTO;
import com.nutritionlabs.domain.users.dto.UserTagCreationParamsDTO;
import com.nutritionlabs.domain.users.dto.WorkplaceDTO;
import com.nutritionlabs.domain.users.entity.AllergyEntity;
import com.nutritionlabs.domain.users.entity.AppointmentInformationEntity;
import com.nutritionlabs.domain.users.entity.ClinicalGoalEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryEntity;
import com.nutritionlabs.domain.users.entity.DiseaseEntity;
import com.nutritionlabs.domain.users.entity.FoodIntoleranceEntity;
import com.nutritionlabs.domain.users.entity.MedicalHistoryEntity;
import com.nutritionlabs.domain.users.entity.NutritionalDeficiencyEntity;
import com.nutritionlabs.domain.users.entity.ObservationEntity;
import com.nutritionlabs.domain.users.entity.PersonalHistoryEntity;
import com.nutritionlabs.domain.users.entity.TypeDieteEntity;
import com.nutritionlabs.domain.users.entity.UserEntity;
import com.nutritionlabs.domain.users.entity.UserTagEntity;
import com.nutritionlabs.domain.users.entity.WorkplaceEntity;

@Mapper
public interface UsersMapper {

	public UsersMapper MAPPER = Mappers.getMapper(UsersMapper.class);

	@Mapping(source = "workplace.id", target = "idWorkplace")
	UserDTO userEntityToDTO(UserEntity userEntity);

	WorkplaceDTO workplaceEntityToDTO(WorkplaceEntity workplaceEntity);

	AppointmentInformationDTO appointmentInformationToDTO(AppointmentInformationEntity appointmentInformation);

	ClinicalGoalDTO clinicalGoalToDTO(ClinicalGoalEntity clinicalGoal);

	PersonalHistoryDTO personalHistoryToDTO(PersonalHistoryEntity personalHistory);

	DietaryHistoryDTO dietaryHistoryToDTO(DietaryHistoryEntity dietaryHistory);

	TypeDieteDTO typeDieteToDTO(TypeDieteEntity typeDiete);

	AllergyDTO allergyToDTO(AllergyEntity allergyEntity);

	FoodIntoleranceDTO foodIntoleranceToDTO(FoodIntoleranceEntity foodIntoleranceEntity);

	NutritionalDeficiencyDTO nutritionalDeficiencyToDTO(NutritionalDeficiencyEntity nutritionalDeficiencyEntity);

	MedicalHistoryDTO medicalHistoryToDTO(MedicalHistoryEntity medicalHistory);

	DiseaseDTO diseaseToDTO(DiseaseEntity disease);

	ObservationDTO toObservationDTO(ObservationEntity observationEntity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userEntities", ignore = true)
	@Mapping(target = "version", ignore = true)
	UserTagEntity tagParametersToUserTagEntity(UserTagCreationParamsDTO tagCreationParamsDTO);
}
