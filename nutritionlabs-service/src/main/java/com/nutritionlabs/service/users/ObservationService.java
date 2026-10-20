package com.nutritionlabs.service.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.users.ObservationBusinessService;
import com.nutritionlabs.domain.users.dto.CreateObservationParamsDTO;
import com.nutritionlabs.domain.users.dto.ObservationDTO;
import com.nutritionlabs.domain.users.dto.UpdateObservationParamsDTO;
import com.nutritionlabs.service.ResourcePaths;

/**
 * Observations registered for a patient.
 */
@RestController
@RequestMapping(ResourcePaths.USER + "/user/{userId}/observations")
public class ObservationService {

	@Autowired
	private ObservationBusinessService observationBusinessService;

	@GetMapping
	public Page<ObservationDTO> getObservationsEntities(@PathVariable Long userId, Pageable pageable) {
		return observationBusinessService.getObservations(userId, pageable);
	}

	@PostMapping
	public ObservationDTO createObservation(@PathVariable Long userId, @RequestBody CreateObservationParamsDTO params) {
		return observationBusinessService.createObservation(userId, params);
	}

	@PutMapping
	public ObservationDTO updateObservation(@PathVariable Long userId, @RequestBody UpdateObservationParamsDTO params) {
		return observationBusinessService.updateObservation(params);
	}

	@DeleteMapping("/{observationId}")
	public ObservationDTO deleteObservation(@PathVariable Long userId, @PathVariable Long observationId) {
		return observationBusinessService.deleteObservation(observationId);
	}
}
