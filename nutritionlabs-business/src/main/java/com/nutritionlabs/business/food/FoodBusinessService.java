package com.nutritionlabs.business.food;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.dataaccess.food.FoodRepository;
import com.nutritionlabs.domain.food.dto.FoodDTO;
import com.nutritionlabs.domain.food.dto.FoodDomainParametersDTO;
import com.nutritionlabs.domain.food.entity.FoodCommonMeasureEntity;
import com.nutritionlabs.domain.food.entity.FoodEntity;
import com.nutritionlabs.domain.food.entity.FoodNutrimentEntity;
import com.nutritionlabs.domain.food.entity.FoodUser;
import com.nutritionlabs.domain.food.entity.FoodUserPkEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.food.FoodToFoodDTOMapper;

/**
 * Foods and supplements. Foods without owner are shared by every user, the rest belong to the user that
 * created them.
 */
@Service
@Transactional
public class FoodBusinessService {

	private static final String NOT_AUTHORIZED_MESSAGE = "User not authorized to modify this entity";

	@Autowired
	private FoodRepository foodRepository;

	@Transactional(readOnly = true)
	public Page<FoodDTO> getFoodEntities(FoodDomainParametersDTO foodDomainParametersDTO) {
		return foodRepository.findFoodsWithNutriments(foodDomainParametersDTO);
	}

	@Transactional(readOnly = true)
	public FoodDTO getFoodEntity(Long id) {
		return foodRepository.findById(id)
				.map(FoodToFoodDTOMapper.INSTANCE::foodEntityToFoodDTOWithNutriments)
				.orElseThrow(() -> new EntityNotFoundException(FoodEntity.class, "id", String.valueOf(id)));
	}

	public void createFoodEntity(FoodEntity foodEntity, String username) {
		FoodUserPkEntity foodUserPkEntity = FoodUserPkEntity.builder().username(username).build();
		FoodUser foodUser = FoodUser.builder().foodUserPkEntity(foodUserPkEntity).foodEntity(foodEntity).build();
		foodEntity.setId(null);
		foodEntity.setFoodUsers(new ArrayList<>(List.of(foodUser)));
		foodEntity.setCommonMeasures(emptyIfNull(foodEntity.getCommonMeasures()));
		foodEntity.setNutriments(emptyIfNull(foodEntity.getNutriments()));
		foodEntity.getCommonMeasures().forEach(commonMeasure -> commonMeasure.setFoodEntity(foodEntity));
		foodEntity.getNutriments().forEach(nutriment -> nutriment.setFoodEntity(foodEntity));
		foodRepository.save(foodEntity);
	}

	public void updateFoodEntity(FoodEntity foodEntity, String username) {
		foodRepository.findById(foodEntity.getId()).ifPresent(food -> {
			validateOwner(username, food);
			setContents(foodEntity, food);
			foodRepository.save(food);
		});
	}

	public void deleteFoodEntity(Long id, String username) {
		foodRepository.findById(id).ifPresent(food -> {
			validateOwner(username, food);
			foodRepository.delete(food);
		});
	}

	private void validateOwner(String username, FoodEntity food) {
		boolean owner = food.getFoodUsers().stream()
				.anyMatch(user -> username.equalsIgnoreCase(user.getFoodUserPkEntity().getUsername()));
		if (!owner) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_AUTHORIZED_MESSAGE);
		}
	}

	private void setContents(FoodEntity foodOrigin, FoodEntity foodDestiny) {
		foodDestiny.setName(foodOrigin.getName());
		foodDestiny.setFoodGroup(foodOrigin.getFoodGroup());
		List<FoodNutrimentEntity> nutrimentsOrigin = emptyIfNull(foodOrigin.getNutriments());
		List<FoodCommonMeasureEntity> commonMeasuresOrigin = emptyIfNull(foodOrigin.getCommonMeasures());
		nutrimentsOrigin.forEach(nutriment -> nutriment.setFoodEntity(foodDestiny));
		commonMeasuresOrigin.forEach(commonMeasure -> commonMeasure.setFoodEntity(foodDestiny));
		setNutrimentContents(nutrimentsOrigin, foodDestiny.getNutriments());
		setCommonMeasureContents(commonMeasuresOrigin, foodDestiny.getCommonMeasures());
	}

	private void setCommonMeasureContents(List<FoodCommonMeasureEntity> commonMeasuresOrigin, List<FoodCommonMeasureEntity> commonMeasuresDestiny) {
		commonMeasuresDestiny.removeIf(commonMeasure -> commonMeasuresOrigin.stream()
				.noneMatch(commonMeasureOrigin -> Objects.equals(commonMeasureOrigin.getId(), commonMeasure.getId())));
		commonMeasuresDestiny.forEach(commonMeasureDestiny -> commonMeasuresOrigin.stream()
				.filter(commonMeasureOrigin -> Objects.equals(commonMeasureDestiny.getId(), commonMeasureOrigin.getId()))
				.findFirst()
				.ifPresent(commonMeasureOrigin -> {
					commonMeasureDestiny.setSingularName(commonMeasureOrigin.getSingularName());
					commonMeasureDestiny.setPluralName(commonMeasureOrigin.getPluralName());
					commonMeasureDestiny.setQuantity(commonMeasureOrigin.getQuantity());
					commonMeasureDestiny.setTotalGrams(commonMeasureOrigin.getTotalGrams());
					commonMeasureDestiny.setEdiblePortion(commonMeasureOrigin.getEdiblePortion());
				}));
		commonMeasuresDestiny.addAll(commonMeasuresOrigin.stream()
				.filter(commonMeasureOrigin -> commonMeasureOrigin.getId() == null)
				.collect(Collectors.toList()));
	}

	private void setNutrimentContents(List<FoodNutrimentEntity> foodNutrimentsOrigin, List<FoodNutrimentEntity> foodNutrimentsDestiny) {
		foodNutrimentsDestiny.removeIf(foodNutriment -> foodNutrimentsOrigin.stream()
				.noneMatch(nutriment -> nutriment.getIdNutrimentCatalog() == foodNutriment.getIdNutrimentCatalog()));
		foodNutrimentsDestiny.forEach(nutriment -> foodNutrimentsOrigin.stream()
				.filter(nutrimentOrigin -> nutriment.getIdNutrimentCatalog() == nutrimentOrigin.getIdNutrimentCatalog())
				.findFirst()
				.ifPresent(nutrimentOrigin -> nutriment.setQuantity(nutrimentOrigin.getQuantity())));
		List<Integer> idsNutrimentsDestiny = foodNutrimentsDestiny.stream().map(FoodNutrimentEntity::getIdNutrimentCatalog).collect(Collectors.toList());
		foodNutrimentsDestiny.addAll(foodNutrimentsOrigin.stream()
				.filter(nutriment -> !idsNutrimentsDestiny.contains(nutriment.getIdNutrimentCatalog()))
				.peek(nutriment -> nutriment.setId(null))
				.collect(Collectors.toList()));
	}

	private static <T> List<T> emptyIfNull(List<T> list) {
		return list == null ? new ArrayList<>() : list;
	}
}
