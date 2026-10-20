package com.nutritionlabs.business.users;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.business.common.ValueModificatorService;
import com.nutritionlabs.dataaccess.users.AllergyRepository;
import com.nutritionlabs.dataaccess.users.DietaryHistoryAllergiesRepository;
import com.nutritionlabs.dataaccess.users.DietaryHistoryFoodIntoleranceRepository;
import com.nutritionlabs.dataaccess.users.DietaryHistoryNutritionalDeficiencyRepository;
import com.nutritionlabs.dataaccess.users.DietaryHistoryRepository;
import com.nutritionlabs.dataaccess.users.DietaryHistoryTypesDieteRepository;
import com.nutritionlabs.dataaccess.users.FoodIntoleranceRepository;
import com.nutritionlabs.dataaccess.users.NutritionalDeficiencyRepository;
import com.nutritionlabs.dataaccess.users.TypeDieteRepository;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.users.dto.AllergyDTO;
import com.nutritionlabs.domain.users.dto.DietaryHistoryDTO;
import com.nutritionlabs.domain.users.dto.FoodIntoleranceDTO;
import com.nutritionlabs.domain.users.dto.NutritionalDeficiencyDTO;
import com.nutritionlabs.domain.users.dto.TypeDieteDTO;
import com.nutritionlabs.domain.users.entity.AllergyEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryAllergiesEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryAllergyPkEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryFoodIntoleranceEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryFoodIntolerancePkEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryNutritionalDeficiencyEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryNutritionalDeficiencyPkEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryTypeDietePkEntity;
import com.nutritionlabs.domain.users.entity.DietaryHistoryTypesDieteEntity;
import com.nutritionlabs.domain.users.entity.FoodIntoleranceEntity;
import com.nutritionlabs.domain.users.entity.NutritionalDeficiencyEntity;
import com.nutritionlabs.domain.users.entity.TypeDieteEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.users.UsersMapper;

@Service
@Transactional(readOnly = true)
public class DietaryHistoryBusinessService {

	@Autowired
	private DietaryHistoryRepository dietaryHistoryRepository;

	@Autowired
	private ValueModificatorService valueModificatorService;

	@Autowired
	private UserHistoriesFactory userHistoriesFactory;

	@Autowired
	private DietaryHistoryTypesDieteRepository dietaryHistoryTypesDieteRepository;

	@Autowired
	private TypeDieteRepository typeDieteRepository;

	@Autowired
	private DietaryHistoryAllergiesRepository dietaryHistoryAllergiesRepository;

	@Autowired
	private AllergyRepository allergyRepository;

	@Autowired
	private DietaryHistoryFoodIntoleranceRepository dietaryHistoryFoodIntoleranceRepository;

	@Autowired
	private FoodIntoleranceRepository foodIntoleranceRepository;

	@Autowired
	private DietaryHistoryNutritionalDeficiencyRepository dietaryHistoryNutritionalDeficiencyRepository;

	@Autowired
	private NutritionalDeficiencyRepository nutritionalDeficiencyRepository;

	public Optional<DietaryHistoryDTO> getDietaryHistory(Long idUser) {
		return dietaryHistoryRepository.findByUserEntity_Id(idUser)
				.map(UsersMapper.MAPPER::dietaryHistoryToDTO);
	}

	@Transactional
	public DietaryHistoryDTO updateDietaryHistory(SingleValueDTO<?> payload) {
		DietaryHistoryEntity dietaryHistory = dietaryHistoryRepository.findById(payload.getId())
				.orElseThrow(() -> new EntityNotFoundException(DietaryHistoryEntity.class, "id", String.valueOf(payload.getId())));
		valueModificatorService.modifyValue(dietaryHistory, payload);
		dietaryHistoryRepository.saveAndFlush(dietaryHistory);
		return UsersMapper.MAPPER.dietaryHistoryToDTO(dietaryHistory);
	}

	// Types diete

	public List<TypeDieteDTO> getTypesDiete(Long idUser) {
		return dietaryHistoryTypesDieteRepository.findByDietaryHistory_UserEntity_Id(idUser)
				.stream()
				.map(DietaryHistoryTypesDieteEntity::getTypeDiete)
				.map(UsersMapper.MAPPER::typeDieteToDTO)
				.collect(Collectors.toList());
	}

	public List<TypeDieteDTO> getAllTypesDiete() {
		return typeDieteRepository.findAll()
				.stream()
				.map(UsersMapper.MAPPER::typeDieteToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public TypeDieteDTO setTypeDiete(Long idUser, Long idTypeDiete) {
		DietaryHistoryEntity dietaryHistory = userHistoriesFactory.getOrCreateDietaryHistory(idUser);
		TypeDieteEntity typeDiete = typeDieteRepository.findById(idTypeDiete)
				.orElseThrow(() -> new EntityNotFoundException(TypeDieteEntity.class, "id", String.valueOf(idTypeDiete)));
		DietaryHistoryTypesDieteEntity dietaryHistoryTypesDiete = DietaryHistoryTypesDieteEntity.builder()
				.dietaryHistoryTypeDietePk(DietaryHistoryTypeDietePkEntity.builder()
						.idDietaryHistory(dietaryHistory.getId())
						.idTypeDiete(typeDiete.getId())
						.build())
				.dietaryHistory(dietaryHistory)
				.typeDiete(typeDiete)
				.build();
		return UsersMapper.MAPPER.typeDieteToDTO(dietaryHistoryTypesDieteRepository.save(dietaryHistoryTypesDiete).getTypeDiete());
	}

	@Transactional
	public void deleteTypeDiete(Long idUser, Long idTypeDiete) {
		dietaryHistoryRepository.findByUserEntity_Id(idUser)
				.flatMap(dietaryHistory -> dietaryHistoryTypesDieteRepository.findByDietaryHistory_IdAndTypeDiete_Id(dietaryHistory.getId(), idTypeDiete))
				.ifPresent(dietaryHistoryTypesDieteRepository::delete);
	}

	// Allergies

	public List<AllergyDTO> getAllergies(Long idUser) {
		return dietaryHistoryAllergiesRepository.findByDietaryHistory_UserEntity_Id(idUser)
				.stream()
				.map(DietaryHistoryAllergiesEntity::getAllergyEntity)
				.map(UsersMapper.MAPPER::allergyToDTO)
				.collect(Collectors.toList());
	}

	public List<AllergyDTO> getAllAllergies() {
		return allergyRepository.findAll()
				.stream()
				.map(UsersMapper.MAPPER::allergyToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public AllergyDTO setAllergy(Long idUser, Long idAllergy) {
		DietaryHistoryEntity dietaryHistory = userHistoriesFactory.getOrCreateDietaryHistory(idUser);
		AllergyEntity allergy = allergyRepository.findById(idAllergy)
				.orElseThrow(() -> new EntityNotFoundException(AllergyEntity.class, "id", String.valueOf(idAllergy)));
		DietaryHistoryAllergiesEntity dietaryHistoryAllergies = DietaryHistoryAllergiesEntity.builder()
				.dietaryHistoryAllergyPkEntity(DietaryHistoryAllergyPkEntity.builder()
						.idDietaryHistory(dietaryHistory.getId())
						.idAllergy(allergy.getId())
						.build())
				.dietaryHistory(dietaryHistory)
				.allergyEntity(allergy)
				.build();
		return UsersMapper.MAPPER.allergyToDTO(dietaryHistoryAllergiesRepository.save(dietaryHistoryAllergies).getAllergyEntity());
	}

	@Transactional
	public void deleteAllergy(Long idUser, Long idAllergy) {
		dietaryHistoryRepository.findByUserEntity_Id(idUser)
				.flatMap(dietaryHistory -> dietaryHistoryAllergiesRepository.findByDietaryHistory_IdAndAllergyEntity_Id(dietaryHistory.getId(), idAllergy))
				.ifPresent(dietaryHistoryAllergiesRepository::delete);
	}

	// Food intolerances

	public List<FoodIntoleranceDTO> getFoodIntolerances(Long idUser) {
		return dietaryHistoryFoodIntoleranceRepository.findByDietaryHistory_UserEntity_Id(idUser)
				.stream()
				.map(DietaryHistoryFoodIntoleranceEntity::getFoodIntoleranceEntity)
				.map(UsersMapper.MAPPER::foodIntoleranceToDTO)
				.collect(Collectors.toList());
	}

	public List<FoodIntoleranceDTO> getAllFoodIntolerances() {
		return foodIntoleranceRepository.findAll()
				.stream()
				.map(UsersMapper.MAPPER::foodIntoleranceToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public FoodIntoleranceDTO setFoodIntolerance(Long idUser, Long idFoodIntolerance) {
		DietaryHistoryEntity dietaryHistory = userHistoriesFactory.getOrCreateDietaryHistory(idUser);
		FoodIntoleranceEntity foodIntolerance = foodIntoleranceRepository.findById(idFoodIntolerance)
				.orElseThrow(() -> new EntityNotFoundException(FoodIntoleranceEntity.class, "id", String.valueOf(idFoodIntolerance)));
		DietaryHistoryFoodIntoleranceEntity dietaryHistoryFoodIntolerance = DietaryHistoryFoodIntoleranceEntity.builder()
				.dietaryHistoryFoodIntolerancePkEntity(DietaryHistoryFoodIntolerancePkEntity.builder()
						.idDietaryHistory(dietaryHistory.getId())
						.idFoodIntolerance(foodIntolerance.getId())
						.build())
				.dietaryHistory(dietaryHistory)
				.foodIntoleranceEntity(foodIntolerance)
				.build();
		return UsersMapper.MAPPER.foodIntoleranceToDTO(dietaryHistoryFoodIntoleranceRepository.save(dietaryHistoryFoodIntolerance).getFoodIntoleranceEntity());
	}

	@Transactional
	public void deleteFoodIntolerance(Long idUser, Long idFoodIntolerance) {
		dietaryHistoryRepository.findByUserEntity_Id(idUser)
				.flatMap(dietaryHistory -> dietaryHistoryFoodIntoleranceRepository
						.findByDietaryHistory_IdAndFoodIntoleranceEntity_Id(dietaryHistory.getId(), idFoodIntolerance))
				.ifPresent(dietaryHistoryFoodIntoleranceRepository::delete);
	}

	// Nutritional deficiencies

	public List<NutritionalDeficiencyDTO> getNutritionalDeficiencies(Long idUser) {
		return dietaryHistoryNutritionalDeficiencyRepository.findByDietaryHistory_UserEntity_Id(idUser)
				.stream()
				.map(DietaryHistoryNutritionalDeficiencyEntity::getNutritionalDeficiencyEntity)
				.map(UsersMapper.MAPPER::nutritionalDeficiencyToDTO)
				.collect(Collectors.toList());
	}

	public List<NutritionalDeficiencyDTO> getAllNutritionalDeficiencies() {
		return nutritionalDeficiencyRepository.findAll()
				.stream()
				.map(UsersMapper.MAPPER::nutritionalDeficiencyToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public NutritionalDeficiencyDTO setNutritionalDeficiency(Long idUser, Long idNutritionalDeficiency) {
		DietaryHistoryEntity dietaryHistory = userHistoriesFactory.getOrCreateDietaryHistory(idUser);
		NutritionalDeficiencyEntity nutritionalDeficiency = nutritionalDeficiencyRepository.findById(idNutritionalDeficiency)
				.orElseThrow(() -> new EntityNotFoundException(NutritionalDeficiencyEntity.class, "id", String.valueOf(idNutritionalDeficiency)));
		DietaryHistoryNutritionalDeficiencyEntity dietaryHistoryNutritionalDeficiency = DietaryHistoryNutritionalDeficiencyEntity.builder()
				.dietaryHistoryNutritionalDeficiencyPkEntity(DietaryHistoryNutritionalDeficiencyPkEntity.builder()
						.idDietaryHistory(dietaryHistory.getId())
						.idNutritionalDeficiency(nutritionalDeficiency.getId())
						.build())
				.dietaryHistory(dietaryHistory)
				.nutritionalDeficiencyEntity(nutritionalDeficiency)
				.build();
		return UsersMapper.MAPPER.nutritionalDeficiencyToDTO(
				dietaryHistoryNutritionalDeficiencyRepository.save(dietaryHistoryNutritionalDeficiency).getNutritionalDeficiencyEntity());
	}

	@Transactional
	public void deleteNutritionalDeficiency(Long idUser, Long idNutritionalDeficiency) {
		dietaryHistoryRepository.findByUserEntity_Id(idUser)
				.flatMap(dietaryHistory -> dietaryHistoryNutritionalDeficiencyRepository
						.findByDietaryHistory_IdAndNutritionalDeficiencyEntity_Id(dietaryHistory.getId(), idNutritionalDeficiency))
				.ifPresent(dietaryHistoryNutritionalDeficiencyRepository::delete);
	}
}
