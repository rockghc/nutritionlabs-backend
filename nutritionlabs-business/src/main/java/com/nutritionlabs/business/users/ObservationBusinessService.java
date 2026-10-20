package com.nutritionlabs.business.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.dataaccess.users.ObservationRepository;
import com.nutritionlabs.dataaccess.users.UserRepository;
import com.nutritionlabs.domain.users.dto.CreateObservationParamsDTO;
import com.nutritionlabs.domain.users.dto.ObservationDTO;
import com.nutritionlabs.domain.users.dto.UpdateObservationParamsDTO;
import com.nutritionlabs.domain.users.entity.ObservationEntity;
import com.nutritionlabs.domain.users.entity.UserEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.users.UsersMapper;

@Service
@Transactional(readOnly = true)
public class ObservationBusinessService {

	@Autowired
	private ObservationRepository observationRepository;

	@Autowired
	private UserRepository userRepository;

	public Page<ObservationDTO> getObservations(Long idUser, Pageable pageable) {
		return observationRepository.findByUserEntity_IdOrderByIdDesc(idUser, pageable)
				.map(UsersMapper.MAPPER::toObservationDTO);
	}

	@Transactional
	public ObservationDTO updateObservation(UpdateObservationParamsDTO params) {
		return observationRepository.findById(params.getId())
				.map(observationEntity -> {
					observationEntity.setObservation(params.getObservation());
					observationEntity.setRegistrationDate(params.getRegistrationDate());
					return observationRepository.save(observationEntity);
				})
				.map(UsersMapper.MAPPER::toObservationDTO)
				.orElse(null);
	}

	@Transactional
	public ObservationDTO createObservation(Long idUser, CreateObservationParamsDTO params) {
		UserEntity userEntity = userRepository.findById(idUser)
				.orElseThrow(() -> new EntityNotFoundException(UserEntity.class, "id", String.valueOf(idUser)));
		ObservationEntity observationEntity = observationRepository.save(ObservationEntity.builder()
				.observation(params.getObservation())
				.registrationDate(params.getRegistrationDate())
				.userEntity(userEntity)
				.build());
		return UsersMapper.MAPPER.toObservationDTO(observationEntity);
	}

	@Transactional
	public ObservationDTO deleteObservation(Long idObservation) {
		return observationRepository.findById(idObservation)
				.map(observationEntity -> {
					observationRepository.delete(observationEntity);
					return UsersMapper.MAPPER.toObservationDTO(observationEntity);
				})
				.orElse(null);
	}
}
