package com.nutritionlabs.dataaccess.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nutritionlabs.domain.users.entity.AppointmentClinicalGoals;
import com.nutritionlabs.domain.users.entity.AppointmentClinicalGoalPkEntity;

public interface AppointmentClinicalGoalsRepository extends JpaRepository<AppointmentClinicalGoals, AppointmentClinicalGoalPkEntity> {

	List<AppointmentClinicalGoals> findByAppointmentInformation_UserEntity_Id(Long idUser);

	Optional<AppointmentClinicalGoals> findByAppointmentInformation_IdAndClinicalGoal_Id(Long idAppointmentInformation, Long idClinicalGoal);
}
