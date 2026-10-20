package com.nutritionlabs.business.users;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.business.common.ValueModificatorService;
import com.nutritionlabs.dataaccess.users.AppointmentClinicalGoalsRepository;
import com.nutritionlabs.dataaccess.users.AppointmentInformationRepository;
import com.nutritionlabs.dataaccess.users.ClinicalGoalRepository;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.users.dto.AppointmentInformationDTO;
import com.nutritionlabs.domain.users.dto.ClinicalGoalDTO;
import com.nutritionlabs.domain.users.entity.AppointmentClinicalGoalPkEntity;
import com.nutritionlabs.domain.users.entity.AppointmentClinicalGoals;
import com.nutritionlabs.domain.users.entity.AppointmentInformationEntity;
import com.nutritionlabs.domain.users.entity.ClinicalGoalEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.users.UsersMapper;

@Service
@Transactional(readOnly = true)
public class AppointmentInformationBusinessService {

	@Autowired
	private AppointmentClinicalGoalsRepository appointmentClinicalGoalsRepository;

	@Autowired
	private AppointmentInformationRepository appointmentInformationRepository;

	@Autowired
	private ClinicalGoalRepository clinicalGoalRepository;

	@Autowired
	private ValueModificatorService valueModificatorService;

	@Autowired
	private UserHistoriesFactory userHistoriesFactory;

	public Optional<AppointmentInformationDTO> getAppointmentInformation(Long idUser) {
		return appointmentInformationRepository.findByUserEntity_Id(idUser)
				.map(UsersMapper.MAPPER::appointmentInformationToDTO);
	}

	public List<ClinicalGoalDTO> getClinicalGoals(Long idUser) {
		return appointmentClinicalGoalsRepository.findByAppointmentInformation_UserEntity_Id(idUser)
				.stream()
				.map(AppointmentClinicalGoals::getClinicalGoal)
				.map(UsersMapper.MAPPER::clinicalGoalToDTO)
				.collect(Collectors.toList());
	}

	public List<ClinicalGoalDTO> getAllClinicalGoals() {
		return clinicalGoalRepository.findAll()
				.stream()
				.map(UsersMapper.MAPPER::clinicalGoalToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public AppointmentInformationDTO updateAppointmentInformation(SingleValueDTO<?> payload) {
		AppointmentInformationEntity appointmentInformation = appointmentInformationRepository.findById(payload.getId())
				.orElseThrow(() -> new EntityNotFoundException(AppointmentInformationEntity.class, "id", String.valueOf(payload.getId())));
		valueModificatorService.modifyValue(appointmentInformation, payload);
		appointmentInformationRepository.saveAndFlush(appointmentInformation);
		return UsersMapper.MAPPER.appointmentInformationToDTO(appointmentInformation);
	}

	@Transactional
	public ClinicalGoalDTO setClinicalGoal(Long idUser, Long idClinicalGoal) {
		AppointmentInformationEntity appointmentInformation = userHistoriesFactory.getOrCreateAppointmentInformation(idUser);
		ClinicalGoalEntity clinicalGoal = clinicalGoalRepository.findById(idClinicalGoal)
				.orElseThrow(() -> new EntityNotFoundException(ClinicalGoalEntity.class, "id", String.valueOf(idClinicalGoal)));
		AppointmentClinicalGoals appointmentClinicalGoals = AppointmentClinicalGoals.builder()
				.appointmentClinicalGoalPk(AppointmentClinicalGoalPkEntity.builder()
						.idAppointmentInformation(appointmentInformation.getId())
						.idClinicalGoal(clinicalGoal.getId())
						.build())
				.appointmentInformation(appointmentInformation)
				.clinicalGoal(clinicalGoal)
				.build();
		return UsersMapper.MAPPER.clinicalGoalToDTO(appointmentClinicalGoalsRepository.save(appointmentClinicalGoals).getClinicalGoal());
	}

	@Transactional
	public void deleteClinicalGoal(Long idUser, Long idClinicalGoal) {
		appointmentInformationRepository.findByUserEntity_Id(idUser)
				.flatMap(appointmentInformation -> appointmentClinicalGoalsRepository
						.findByAppointmentInformation_IdAndClinicalGoal_Id(appointmentInformation.getId(), idClinicalGoal))
				.ifPresent(appointmentClinicalGoalsRepository::delete);
	}
}
