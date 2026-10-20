package com.nutritionlabs.service.users;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.users.AppointmentInformationBusinessService;
import com.nutritionlabs.business.users.DietaryHistoryBusinessService;
import com.nutritionlabs.business.users.MedicalHistoryBusinessService;
import com.nutritionlabs.business.users.PersonalHistoryBusinessService;
import com.nutritionlabs.business.users.UserBusinessService;
import com.nutritionlabs.domain.common.EntityTypes;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.users.dto.AllergyDTO;
import com.nutritionlabs.domain.users.dto.AppointmentInformationDTO;
import com.nutritionlabs.domain.users.dto.ClinicalGoalDTO;
import com.nutritionlabs.domain.users.dto.CreateAppointmentInformationClinicalGoalsDTO;
import com.nutritionlabs.domain.users.dto.CreateDietaryHistoryAllergyDTO;
import com.nutritionlabs.domain.users.dto.CreateDietaryHistoryFoodIntoleranceDTO;
import com.nutritionlabs.domain.users.dto.CreateDietaryHistoryNutritionalDeficiencyDTO;
import com.nutritionlabs.domain.users.dto.CreateDietaryHistoryTypesDieteDTO;
import com.nutritionlabs.domain.users.dto.CreateMedicalHistoryDiseaseDTO;
import com.nutritionlabs.domain.users.dto.DietaryHistoryDTO;
import com.nutritionlabs.domain.users.dto.DiseaseDTO;
import com.nutritionlabs.domain.users.dto.FoodIntoleranceDTO;
import com.nutritionlabs.domain.users.dto.MedicalHistoryDTO;
import com.nutritionlabs.domain.users.dto.NutritionalDeficiencyDTO;
import com.nutritionlabs.domain.users.dto.PersonalHistoryDTO;
import com.nutritionlabs.domain.users.dto.TypeDieteDTO;
import com.nutritionlabs.domain.users.dto.UpdateUserWorkplaceParamsDTO;
import com.nutritionlabs.domain.users.dto.UserDTO;
import com.nutritionlabs.domain.users.dto.WorkplaceDTO;
import com.nutritionlabs.domain.users.entity.UserEntity;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Patients and their clinical histories.
 */
@RestController
@RequestMapping(ResourcePaths.USER + "/user")
public class UserService {

	private static final String WORKPLACE_PROPERTY = "idWorkplace";

	@Autowired
	private UserBusinessService userBusinessService;

	@Autowired
	private AppointmentInformationBusinessService appointmentInformationBusinessService;

	@Autowired
	private PersonalHistoryBusinessService personalHistoryBusinessService;

	@Autowired
	private DietaryHistoryBusinessService dietaryHistoryBusinessService;

	@Autowired
	private MedicalHistoryBusinessService medicalHistoryBusinessService;

	@Autowired
	private SecurityUtils securityUtils;

	// Users

	@GetMapping
	public List<UserDTO> getUsersEntities(Principal principal) {
		return userBusinessService.getUsersEntities(securityUtils.getUsername(principal));
	}

	@GetMapping("/{id}")
	public UserDTO getUserEntity(@PathVariable Long id) {
		return userBusinessService.getUserEntity(id);
	}

	@PostMapping
	public UserDTO createUser(@RequestBody UserEntity userEntity, Principal principal) {
		return userBusinessService.createUser(userEntity, securityUtils.getUsername(principal));
	}

	@PatchMapping
	public UserDTO updateUserAttribute(@RequestBody SingleValueDTO<?> singleValueDTO, Principal principal) {
		if (EntityTypes.USER.equals(singleValueDTO.getType()) && WORKPLACE_PROPERTY.equals(singleValueDTO.getPropertyName())) {
			userBusinessService.updateWorkplace(singleValueDTO.getId(), Long.parseLong(String.valueOf(singleValueDTO.getValue())),
					securityUtils.getUsername(principal));
			return userBusinessService.getUserEntity(singleValueDTO.getId());
		}
		return userBusinessService.updateUser(singleValueDTO);
	}

	@PutMapping("/{userId}:updateWorkplace")
	public void updateWorkplace(@PathVariable Long userId, @RequestBody UpdateUserWorkplaceParamsDTO updateUserWorkplaceParamsDTO, Principal principal) {
		userBusinessService.updateWorkplace(userId, updateUserWorkplaceParamsDTO.getId(), securityUtils.getUsername(principal));
	}

	@GetMapping("/workplace")
	public List<WorkplaceDTO> getWorkplacesEntities(Principal principal) {
		return userBusinessService.getWorkplacesEntities(securityUtils.getUsername(principal));
	}

	// Appointment information

	@GetMapping("/{userId}/appointmentinformation")
	public AppointmentInformationDTO getAppointmentInformation(@PathVariable Long userId) {
		return appointmentInformationBusinessService.getAppointmentInformation(userId).orElse(null);
	}

	@PatchMapping("/appointmentinformation")
	public AppointmentInformationDTO updateAppointmentInformationUserAttribute(@RequestBody SingleValueDTO<?> singleValueDTO) {
		return appointmentInformationBusinessService.updateAppointmentInformation(singleValueDTO);
	}

	@GetMapping("/clinicalgoals")
	public List<ClinicalGoalDTO> getAllClinicalGoals() {
		return appointmentInformationBusinessService.getAllClinicalGoals();
	}

	@GetMapping("/{userId}/appointmentinformation/clinicalgoals")
	public List<ClinicalGoalDTO> getClinicalGoals(@PathVariable Long userId) {
		return appointmentInformationBusinessService.getClinicalGoals(userId);
	}

	@PostMapping("/{idUser}/appointmentinformation/clinicalgoal")
	public ClinicalGoalDTO createAppointmentInformationClinicalGoal(@PathVariable Long idUser, @RequestBody CreateAppointmentInformationClinicalGoalsDTO params) {
		return appointmentInformationBusinessService.setClinicalGoal(idUser, params.getIdClinicalGoal());
	}

	@DeleteMapping("/{idUser}/appointmentinformation/clinicalgoal/{idClinicalGoal}")
	public void deleteAppointmentInformationClinicalGoal(@PathVariable Long idUser, @PathVariable Long idClinicalGoal) {
		appointmentInformationBusinessService.deleteClinicalGoal(idUser, idClinicalGoal);
	}

	// Personal history

	@GetMapping("/{userId}/personalhistory")
	public PersonalHistoryDTO getPersonalHistory(@PathVariable Long userId) {
		return personalHistoryBusinessService.getPersonalHistory(userId).orElse(null);
	}

	@PatchMapping("/personalhistory")
	public PersonalHistoryDTO updatePersonalHistoryUserAttribute(@RequestBody SingleValueDTO<?> singleValueDTO) {
		return personalHistoryBusinessService.updatePersonalHistory(singleValueDTO);
	}

	// Dietary history

	@GetMapping("/{userId}/dietaryhistory")
	public DietaryHistoryDTO getDietaryHistory(@PathVariable Long userId) {
		return dietaryHistoryBusinessService.getDietaryHistory(userId).orElse(null);
	}

	@PatchMapping("/dietaryhistory")
	public DietaryHistoryDTO updateDietaryHistoryUserAttribute(@RequestBody SingleValueDTO<?> singleValueDTO) {
		return dietaryHistoryBusinessService.updateDietaryHistory(singleValueDTO);
	}

	@GetMapping("/typesdiete")
	public List<TypeDieteDTO> getAllTypesDiete() {
		return dietaryHistoryBusinessService.getAllTypesDiete();
	}

	@GetMapping("/{userId}/dietaryhistory/typesdiete")
	public List<TypeDieteDTO> getTypesDiete(@PathVariable Long userId) {
		return dietaryHistoryBusinessService.getTypesDiete(userId);
	}

	@PostMapping("/{idUser}/dietaryhistory/typesdiete")
	public TypeDieteDTO createDietaryHistoryTypeDiete(@PathVariable Long idUser, @RequestBody CreateDietaryHistoryTypesDieteDTO params) {
		return dietaryHistoryBusinessService.setTypeDiete(idUser, params.getIdTypeDiete());
	}

	@DeleteMapping("/{idUser}/dietaryhistory/typediete/{idTypeDiete}")
	public void deleteDietaryHistoryTypeDiete(@PathVariable Long idUser, @PathVariable Long idTypeDiete) {
		dietaryHistoryBusinessService.deleteTypeDiete(idUser, idTypeDiete);
	}

	@GetMapping("/allergies")
	public List<AllergyDTO> getAllAllergies() {
		return dietaryHistoryBusinessService.getAllAllergies();
	}

	@GetMapping("/{userId}/dietaryhistory/allergies")
	public List<AllergyDTO> getAllergies(@PathVariable Long userId) {
		return dietaryHistoryBusinessService.getAllergies(userId);
	}

	@PostMapping("/{idUser}/dietaryhistory/allergy")
	public AllergyDTO createDietaryHistoryAllergy(@PathVariable Long idUser, @RequestBody CreateDietaryHistoryAllergyDTO params) {
		return dietaryHistoryBusinessService.setAllergy(idUser, params.getIdAllergy());
	}

	@DeleteMapping("/{idUser}/dietaryhistory/allergy/{idAllergy}")
	public void deleteDietaryHistoryAllergy(@PathVariable Long idUser, @PathVariable Long idAllergy) {
		dietaryHistoryBusinessService.deleteAllergy(idUser, idAllergy);
	}

	@GetMapping("/foodintolerances")
	public List<FoodIntoleranceDTO> getAllFoodIntolerances() {
		return dietaryHistoryBusinessService.getAllFoodIntolerances();
	}

	@GetMapping("/{userId}/dietaryhistory/foodintolerances")
	public List<FoodIntoleranceDTO> getFoodIntolerances(@PathVariable Long userId) {
		return dietaryHistoryBusinessService.getFoodIntolerances(userId);
	}

	@PostMapping("/{idUser}/dietaryhistory/foodintolerance")
	public FoodIntoleranceDTO createDietaryHistoryFoodIntolerance(@PathVariable Long idUser, @RequestBody CreateDietaryHistoryFoodIntoleranceDTO params) {
		return dietaryHistoryBusinessService.setFoodIntolerance(idUser, params.getIdFoodIntolerance());
	}

	@DeleteMapping("/{idUser}/dietaryhistory/foodintolerance/{idFoodIntolerance}")
	public void deleteDietaryHistoryFoodIntolerance(@PathVariable Long idUser, @PathVariable Long idFoodIntolerance) {
		dietaryHistoryBusinessService.deleteFoodIntolerance(idUser, idFoodIntolerance);
	}

	@GetMapping("/nutritionaldeficiencies")
	public List<NutritionalDeficiencyDTO> getAllNutritionalDeficiencies() {
		return dietaryHistoryBusinessService.getAllNutritionalDeficiencies();
	}

	@GetMapping("/{userId}/dietaryhistory/nutritionaldeficiencies")
	public List<NutritionalDeficiencyDTO> getNutritionalDeficiencies(@PathVariable Long userId) {
		return dietaryHistoryBusinessService.getNutritionalDeficiencies(userId);
	}

	@PostMapping("/{idUser}/dietaryhistory/nutritionaldeficiency")
	public NutritionalDeficiencyDTO createDietaryHistoryNutritionalDeficiency(@PathVariable Long idUser,
			@RequestBody CreateDietaryHistoryNutritionalDeficiencyDTO params) {
		return dietaryHistoryBusinessService.setNutritionalDeficiency(idUser, params.getIdNutritionalDeficiency());
	}

	@DeleteMapping("/{idUser}/dietaryhistory/nutritionaldeficiency/{idNutritionalDeficiency}")
	public void deleteDietaryHistoryNutritionalDeficiency(@PathVariable Long idUser, @PathVariable Long idNutritionalDeficiency) {
		dietaryHistoryBusinessService.deleteNutritionalDeficiency(idUser, idNutritionalDeficiency);
	}

	// Medical history

	@GetMapping("/{userId}/medicalhistory")
	public MedicalHistoryDTO getMedicalHistory(@PathVariable Long userId) {
		return medicalHistoryBusinessService.getMedicalHistory(userId).orElse(null);
	}

	@PatchMapping("/medicalhistory")
	public MedicalHistoryDTO updateMedicalHistoryUserAttribute(@RequestBody SingleValueDTO<?> singleValueDTO) {
		return medicalHistoryBusinessService.updateMedicalHistory(singleValueDTO);
	}

	@GetMapping("/diseases")
	public List<DiseaseDTO> getAllDiseases() {
		return medicalHistoryBusinessService.getAllDiseases();
	}

	@GetMapping("/{userId}/medicalhistory/diseases")
	public List<DiseaseDTO> getDiseases(@PathVariable Long userId) {
		return medicalHistoryBusinessService.getDiseases(userId);
	}

	@PostMapping("/{idUser}/medicalhistory/diseases")
	public DiseaseDTO createMedicalHistoryDisease(@PathVariable Long idUser, @RequestBody CreateMedicalHistoryDiseaseDTO params) {
		return medicalHistoryBusinessService.setDisease(idUser, params.getIdDisease());
	}

	@DeleteMapping("/{idUser}/medicalhistory/disease/{idDisease}")
	public void deleteMedicalHistoryDisease(@PathVariable Long idUser, @PathVariable Long idDisease) {
		medicalHistoryBusinessService.deleteDisease(idUser, idDisease);
	}
}
