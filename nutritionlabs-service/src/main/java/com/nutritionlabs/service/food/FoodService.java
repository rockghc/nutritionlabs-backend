package com.nutritionlabs.service.food;

import java.security.Principal;
import java.util.Optional;

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

import com.nutritionlabs.business.food.FoodBusinessService;
import com.nutritionlabs.domain.food.dto.FoodDTO;
import com.nutritionlabs.domain.food.dto.FoodDomainParametersDTO;
import com.nutritionlabs.domain.food.entity.FoodEntity;
import com.nutritionlabs.domain.food.entity.SupplementEntity;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Foods and supplements.
 */
@RestController
@RequestMapping(ResourcePaths.FOOD)
public class FoodService {

	private static final String FOOD_TYPE_FOOD = "FOOD";
	private static final String FOOD_TYPE_SUPPLEMENT = "SUPPLEMENT";

	@Autowired
	private FoodBusinessService foodBusinessService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping("/food")
	public Page<FoodDTO> getFoodsPaginatedByUser(Pageable pageable, Principal principal, FoodRestServiceParametersDTO parameters) {
		return foodBusinessService.getFoodEntities(toDomainParameters(pageable, securityUtils.getUsername(principal), FOOD_TYPE_FOOD, parameters));
	}

	@GetMapping("/food/{id}")
	public FoodDTO getFood(@PathVariable Long id) {
		return foodBusinessService.getFoodEntity(id);
	}

	@PostMapping("/food")
	public void createFood(@RequestBody FoodEntity foodEntity, Principal principal) {
		foodBusinessService.createFoodEntity(foodEntity, securityUtils.getUsername(principal));
	}

	@PutMapping("/food")
	public void updateFood(@RequestBody FoodEntity foodEntity, Principal principal) {
		foodBusinessService.updateFoodEntity(foodEntity, securityUtils.getUsername(principal));
	}

	@DeleteMapping("/food/{id}")
	public void deleteFood(@PathVariable Long id, Principal principal) {
		foodBusinessService.deleteFoodEntity(id, securityUtils.getUsername(principal));
	}

	@GetMapping("/supplement")
	public Page<FoodDTO> getSupplementsPaginatedByUser(Pageable pageable, Principal principal, FoodRestServiceParametersDTO parameters) {
		return foodBusinessService.getFoodEntities(toDomainParameters(pageable, securityUtils.getUsername(principal), FOOD_TYPE_SUPPLEMENT, parameters));
	}

	@GetMapping("/supplement/{id}")
	public FoodDTO getSupplement(@PathVariable Long id) {
		return foodBusinessService.getFoodEntity(id);
	}

	@PostMapping("/supplement")
	public void createSupplement(@RequestBody SupplementEntity supplementEntity, Principal principal) {
		foodBusinessService.createFoodEntity(supplementEntity, securityUtils.getUsername(principal));
	}

	@PutMapping("/supplement")
	public void updateSupplement(@RequestBody SupplementEntity supplementEntity, Principal principal) {
		foodBusinessService.updateFoodEntity(supplementEntity, securityUtils.getUsername(principal));
	}

	@DeleteMapping("/supplement/{id}")
	public void deleteSupplement(@PathVariable Long id, Principal principal) {
		foodBusinessService.deleteFoodEntity(id, securityUtils.getUsername(principal));
	}

	private FoodDomainParametersDTO toDomainParameters(Pageable pageable, String username, String foodType, FoodRestServiceParametersDTO parameters) {
		return FoodDomainParametersDTO.builder()
				.pageable(pageable)
				.username(username)
				.foodType(foodType)
				.searchName(Optional.ofNullable(parameters.getSearchName()))
				.source(Optional.ofNullable(parameters.getSource()))
				.withCommonMeasures(Boolean.TRUE.equals(parameters.getWithCommonMeasures()))
				.build();
	}
}
