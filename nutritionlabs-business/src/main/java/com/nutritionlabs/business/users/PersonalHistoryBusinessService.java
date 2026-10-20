package com.nutritionlabs.business.users;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.business.common.ValueModificatorService;
import com.nutritionlabs.dataaccess.users.PersonalHistoryRepository;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.users.dto.PersonalHistoryDTO;
import com.nutritionlabs.domain.users.entity.PersonalHistoryEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.users.UsersMapper;

@Service
@Transactional(readOnly = true)
public class PersonalHistoryBusinessService {

	@Autowired
	private PersonalHistoryRepository personalHistoryRepository;

	@Autowired
	private ValueModificatorService valueModificatorService;

	public Optional<PersonalHistoryDTO> getPersonalHistory(Long idUser) {
		return personalHistoryRepository.findByUserEntity_Id(idUser)
				.map(UsersMapper.MAPPER::personalHistoryToDTO);
	}

	@Transactional
	public PersonalHistoryDTO updatePersonalHistory(SingleValueDTO<?> payload) {
		PersonalHistoryEntity personalHistory = personalHistoryRepository.findById(payload.getId())
				.orElseThrow(() -> new EntityNotFoundException(PersonalHistoryEntity.class, "id", String.valueOf(payload.getId())));
		valueModificatorService.modifyValue(personalHistory, payload);
		personalHistoryRepository.saveAndFlush(personalHistory);
		return UsersMapper.MAPPER.personalHistoryToDTO(personalHistory);
	}
}
