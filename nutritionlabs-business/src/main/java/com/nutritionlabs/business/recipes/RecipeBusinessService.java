package com.nutritionlabs.business.recipes;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.business.catalog.CatalogBusinessService;
import com.nutritionlabs.business.common.ValueModificatorService;
import com.nutritionlabs.business.files.ImageService;
import com.nutritionlabs.business.food.FoodBusinessService;
import com.nutritionlabs.dataaccess.recipes.IngredientFoodRepository;
import com.nutritionlabs.dataaccess.recipes.RecipeIngredientRepository;
import com.nutritionlabs.dataaccess.recipes.RecipeRepository;
import com.nutritionlabs.dataaccess.recipes.RecipeTagRepository;
import com.nutritionlabs.domain.catalog.dto.CatalogDetailDTO;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.recipes.dto.CookingMethodDTO;
import com.nutritionlabs.domain.recipes.dto.CreateCookingMethodParametersDTO;
import com.nutritionlabs.domain.recipes.dto.CreateRecipeCommonMeasureParametersDTO;
import com.nutritionlabs.domain.recipes.dto.IngredientFoodReportDTO;
import com.nutritionlabs.domain.recipes.dto.MacronutrimentsDistributionDTO;
import com.nutritionlabs.domain.recipes.dto.MacronutrimentsDistributionStatisticsDTO;
import com.nutritionlabs.domain.recipes.dto.MacronutrimentsStatisticsDTO;
import com.nutritionlabs.domain.recipes.dto.MacronutrimentsSummaryStatisticsDTO;
import com.nutritionlabs.domain.recipes.dto.NutritionalInformationDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeCommonMeasureDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeIngredientDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeIngredientParametersDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeReportDTO;
import com.nutritionlabs.domain.recipes.dto.RecipesPaginatedByUserParametersDTO;
import com.nutritionlabs.domain.recipes.dto.UpdateCookingMethodParametersDTO;
import com.nutritionlabs.domain.recipes.dto.UpdateRecipeCommonMeasureParametersDTO;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodCommonMeasureEntity;
import com.nutritionlabs.domain.recipes.entity.IngredientFoodEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeEntity.NutrimentInformation;
import com.nutritionlabs.domain.recipes.entity.RecipeIngredientEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeTagEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeUserEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeUserPkEntity;
import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.mappers.recipes.CookingMethodMapper;
import com.nutritionlabs.mappers.recipes.FoodDTOToIngredientFoodEntityMapper;
import com.nutritionlabs.mappers.recipes.RecipeCommonMeasureMapper;
import com.nutritionlabs.mappers.recipes.RecipeIngredientMapper;
import com.nutritionlabs.mappers.recipes.RecipeToRecipeDTOMapper;

@Service
@Transactional
public class RecipeBusinessService {

	public static final long ID_CATALOG_WEIGHT = 8;
	public static final long ID_CATALOG_WEIGHT_UNIT = 77;
	public static final long ID_CATALOG_MACRONUTRIMENTS = 1;
	public static final long ID_CATALOG_MICRONUTRIMENTS = 4;
	public static final long GRAMS_100 = 100;
	public static final long GRAMS_120 = 120;
	public static final String DRI_CATALOG_TYPE = "dri";
	public static final String PREFIX_CATALOG_TYPE = "PREFIX";
	public static final String UNIT_CATALOG_TYPE = "UNIT";
	public static final String DEFAULT_RECIPE_NAME = "New recipe";
	public static final String COPY_SUFFIX = " (Copy)";
	private static final String DEFAULT_UNIT_LABEL = " grams ";

	/**
	 * Micronutriments shown in the nutritional information of the recipe report.
	 */
	private static final Set<String> REPORT_MICRONUTRIMENTS = Set.of("fatty_acids_total_trans", "choresterol", "sodium", "sugars", "fiber");

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private RecipeIngredientRepository recipeIngredientRepository;

	@Autowired
	private IngredientFoodRepository ingredientFoodRepository;

	@Autowired
	private RecipeTagRepository recipeTagRepository;

	@Autowired
	private ValueModificatorService valueModificatorService;

	@Autowired
	private FoodBusinessService foodBusinessService;

	@Autowired
	private CatalogBusinessService catalogBusinessService;

	@Autowired
	private ImageService imageService;

	public RecipeDTO createRecipeEntity(String username) {
		RecipeEntity recipeEntity = RecipeEntity.builder()
				.name(DEFAULT_RECIPE_NAME)
				.preparationTime(LocalTime.MIDNIGHT)
				.totalTime(LocalTime.MIDNIGHT)
				.isPrivate(true)
				.weight(0d)
				.weightUnit(ID_CATALOG_WEIGHT_UNIT)
				.portions(1L)
				.build();
		recipeEntity.setRecipeUserEntity(new ArrayList<>(List.of(buildRecipeUser(recipeEntity, username))));
		return RecipeToRecipeDTOMapper.MAPPER.recipeEntityToRecipeDTO(recipeRepository.save(recipeEntity));
	}

	@Transactional(readOnly = true)
	public RecipeDTO getRecipeEntity(Long id) {
		return RecipeToRecipeDTOMapper.MAPPER.recipeEntityToRecipeDTO(getRecipe(id));
	}

	@Transactional(readOnly = true)
	public Page<RecipeDTO> getRecipeEntities(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO) {
		return recipeRepository.findRecipesWithNutriments(recipeDomainParametersDTO);
	}

	/**
	 * Data of the recipe report: ingredients, cooking methods, image and nutritional information.
	 */
	@Transactional(readOnly = true)
	public RecipeReportDTO getRecipeReport(Long id) {
		RecipeEntity recipe = getRecipe(id);
		List<List<IngredientFoodReportDTO>> ingredientFoods = recipeIngredientRepository.findByRecipeEntity_IdOrderById(id).stream()
				.map(recipeIngredient -> recipeIngredient.getIngredientFoodEntities().stream()
						.map(ingredientFood -> IngredientFoodReportDTO.builder()
								.id(ingredientFood.getId())
								.name(ingredientFood.getName())
								.build())
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
		byte[] image = Optional.ofNullable(recipe.getImageName())
				.flatMap(imageService::getImageBinaryContent)
				.orElse(null);
		return RecipeReportDTO.builder()
				.idRecipe(id)
				.name(recipe.getName())
				.ingredientFoods(ingredientFoods)
				.cookingMethods(getCookingMethods(id))
				.image(image)
				.portions(recipe.getPortions())
				.nutritionalInformations(getNutritionalInformations(recipe))
				.build();
	}

	public RecipeDTO updateRecipe(SingleValueDTO<?> payload) {
		RecipeEntity recipeEntity = getRecipe(payload.getId());
		valueModificatorService.modifyValue(recipeEntity, payload);
		recipeRepository.saveAndFlush(recipeEntity);
		return RecipeToRecipeDTOMapper.MAPPER.recipeEntityToRecipeDTO(recipeEntity);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RecipeDTO updateRecipeInNewTransaction(SingleValueDTO<?> payload) {
		return updateRecipe(payload);
	}

	public void deleteRecipe(Long idRecipe, String username) {
		RecipeEntity recipeEntity = getRecipe(idRecipe);
		validateRecipeUser(recipeEntity, username);
		List<RecipeTagEntity> tagsOnlyInThisRecipe = recipeEntity.getRecipeTagsEntities()
				.stream()
				.filter(tag -> tag.getRecipeEntities().stream().allMatch(recipe -> recipe.getId().equals(idRecipe)))
				.collect(Collectors.toList());
		recipeEntity.getRecipeTagsEntities().clear();
		recipeRepository.delete(recipeEntity);
		recipeTagRepository.deleteAll(tagsOnlyInThisRecipe);
	}

	/**
	 * Copies the recipe, with its ingredients, tags, cooking methods and common measures, for the user.
	 */
	public RecipeDTO duplicateRecipe(Long idRecipe, String username) {
		RecipeEntity recipeEntity = getRecipe(idRecipe);
		validateRecipeUser(recipeEntity, username);
		RecipeEntity recipeEntityCloned = recipeEntity.clone();
		recipeEntityCloned.setName(recipeEntityCloned.getName() + COPY_SUFFIX);
		recipeEntityCloned.setRecipeUserEntity(new ArrayList<>(List.of(buildRecipeUser(recipeEntityCloned, username))));
		recipeEntityCloned.setRecipeTagsEntities(new ArrayList<>(recipeEntity.getRecipeTagsEntities()));
		recipeEntity.getRecipeIngredientEntities().forEach(recipeIngredient -> {
			RecipeIngredientEntity recipeIngredientCloned = RecipeIngredientEntity.builder()
					.recipeEntity(recipeEntityCloned)
					.build();
			recipeIngredient.getIngredientFoodEntities().forEach(ingredientFood -> {
				IngredientFoodEntity ingredientFoodCloned = ingredientFood.clone();
				ingredientFoodCloned.setRecipeIngredientEntity(recipeIngredientCloned);
				recipeIngredientCloned.getIngredientFoodEntities().add(ingredientFoodCloned);
				setRecipeNutriments(recipeEntityCloned, ingredientFoodCloned);
			});
			recipeEntityCloned.getRecipeIngredientEntities().add(recipeIngredientCloned);
		});
		recipeEntity.getCommonMeasures().forEach(commonMeasure -> {
			var commonMeasureCloned = commonMeasure.clone();
			commonMeasureCloned.setRecipeEntity(recipeEntityCloned);
			recipeEntityCloned.getCommonMeasures().add(commonMeasureCloned);
		});
		recipeEntity.getCookingMethods().forEach(cookingMethod -> {
			var cookingMethodCloned = cookingMethod.clone();
			cookingMethodCloned.setRecipeEntity(recipeEntityCloned);
			recipeEntityCloned.getCookingMethods().add(cookingMethodCloned);
		});
		return RecipeToRecipeDTOMapper.MAPPER.recipeEntityToRecipeDTO(recipeRepository.save(recipeEntityCloned));
	}

	@Transactional(readOnly = true)
	public List<RecipeIngredientDTO> getRecipeIngredients(Long idRecipe) {
		return recipeIngredientRepository.findByRecipeEntity_IdOrderById(idRecipe)
				.stream()
				.map(RecipeIngredientMapper.MAPPER::recipeIngredientEntityToDTOs)
				.collect(Collectors.toList());
	}

	/**
	 * Adds a food to a recipe. When the recipe ingredient is given, the food is added as an alternative of the
	 * foods of that ingredient.
	 *
	 * @return id of the recipe ingredient
	 */
	public String createRecipeIngredient(RecipeIngredientParametersDTO recipeIngredientParametersDTO) {
		RecipeEntity recipeEntity = getRecipe(recipeIngredientParametersDTO.getIdRecipe());
		RecipeIngredientEntity recipeIngredientEntity = getRecipeIngredientEntity(recipeIngredientParametersDTO, recipeEntity);
		IngredientFoodEntity ingredientFoodEntity = FoodDTOToIngredientFoodEntityMapper.foodDTOToIngredientFoodEntity(
				foodBusinessService.getFoodEntity(recipeIngredientParametersDTO.getIdFood()),
				recipeIngredientEntity,
				recipeIngredientParametersDTO.getIdUnit(),
				recipeIngredientParametersDTO.getQuantity());
		ingredientFoodEntity.setQuantity(recipeIngredientParametersDTO.getQuantity() == null ? 1 : recipeIngredientParametersDTO.getQuantity());
		recipeIngredientEntity.getIngredientFoodEntities().add(ingredientFoodEntity);
		if (recipeIngredientEntity.getId() == null) {
			recipeEntity.getRecipeIngredientEntities().add(recipeIngredientEntity);
		}
		RecipeIngredientEntity recipeIngredientSaved = recipeIngredientRepository.save(recipeIngredientEntity);
		setRecipeNutriments(recipeEntity, ingredientFoodEntity);
		recipeRepository.save(recipeEntity);
		return recipeIngredientSaved.getId().toString();
	}

	public void updateRecipeIngredientQuantity(RecipeIngredientParametersDTO recipeIngredientParametersDTO) {
		RecipeEntity recipeEntity = getRecipe(recipeIngredientParametersDTO.getIdRecipe());
		RecipeIngredientEntity recipeIngredientEntity = getRecipeIngredient(recipeIngredientParametersDTO.getIdRecipeIngredient().orElseThrow());
		findIngredientFood(recipeIngredientEntity, recipeIngredientParametersDTO.getIdIngredientFood())
				.ifPresent(ingredientFoodEntity -> {
					removeRecipeNutriment(recipeEntity, ingredientFoodEntity);
					ingredientFoodEntity.setQuantity(recipeIngredientParametersDTO.getQuantity());
					IngredientFoodCommonMeasureEntity selectedCommonMeasure = recipeIngredientParametersDTO.getIdUnit()
							.flatMap(idUnit -> ingredientFoodEntity.getCommonMeasures().stream()
									.filter(commonMeasure -> commonMeasure.getId().equals(idUnit))
									.findAny())
							.orElse(null);
					String selectedCommonMeasureLabel = selectedCommonMeasure != null
							? selectedCommonMeasure.getSingularName().toLowerCase()
							: DEFAULT_UNIT_LABEL;
					ingredientFoodEntity.setIngredientCommonMeasureEntity(selectedCommonMeasure);
					ingredientFoodEntity.setName(ingredientFoodEntity.getQuantity().intValue() + " "
							+ selectedCommonMeasureLabel + " of " + ingredientFoodEntity.getRealName());
					setRecipeNutriments(recipeEntity, ingredientFoodEntity);
				});
		recipeIngredientRepository.save(recipeIngredientEntity);
	}

	public void updateRecipeIngredientName(RecipeIngredientParametersDTO recipeIngredientParametersDTO) {
		RecipeIngredientEntity recipeIngredientEntity = getRecipeIngredient(recipeIngredientParametersDTO.getIdRecipeIngredient().orElseThrow());
		findIngredientFood(recipeIngredientEntity, recipeIngredientParametersDTO.getIdIngredientFood())
				.ifPresent(ingredientFoodEntity -> ingredientFoodEntity.setName(recipeIngredientParametersDTO.getName()));
		recipeIngredientRepository.save(recipeIngredientEntity);
	}

	/**
	 * Removes a food of a recipe ingredient, or the whole ingredient when no food is given. The ingredient is
	 * removed too when it has no foods left.
	 */
	public void deleteRecipeIngredient(Long idRecipe, Long idRecipeIngredient, Optional<Long> idIngredientFood) {
		RecipeIngredientEntity recipeIngredientEntity = getRecipeIngredient(idRecipeIngredient);
		RecipeEntity recipeEntity = getRecipe(idRecipe);
		if (idIngredientFood.isPresent()) {
			findIngredientFood(recipeIngredientEntity, idIngredientFood.get()).ifPresent(ingredientFoodEntity -> {
				removeRecipeNutriment(recipeEntity, ingredientFoodEntity);
				recipeIngredientEntity.getIngredientFoodEntities().remove(ingredientFoodEntity);
				ingredientFoodRepository.delete(ingredientFoodEntity);
			});
		} else {
			recipeIngredientEntity.getIngredientFoodEntities()
					.forEach(ingredientFoodEntity -> removeRecipeNutriment(recipeEntity, ingredientFoodEntity));
		}
		if (idIngredientFood.isEmpty() || recipeIngredientEntity.getIngredientFoodEntities().isEmpty()) {
			recipeEntity.getRecipeIngredientEntities().remove(recipeIngredientEntity);
			recipeIngredientRepository.delete(recipeIngredientEntity);
		}
	}

	@Transactional(readOnly = true)
	public MacronutrimentsStatisticsDTO getMacronutrimentsStatistics(Long idRecipe) {
		RecipeEntity recipeEntity = getRecipe(idRecipe);
		double weight = recipeEntity.getWeight() == null ? 0 : recipeEntity.getWeight();
		double quantityGrams = getQuantityGrams(recipeEntity.getWeightUnit()) * weight;
		Map<Long, Optional<NutrimentInformation>> nutrimentsInformationMap = recipeEntity.getNutrimentsInformation(quantityGrams);
		NutrimentInformation energy = getNutrimentInformation(nutrimentsInformationMap, RecipeEntity.ID_NUTRIMENT_ENERGY);
		NutrimentInformation carbohydrate = getNutrimentInformation(nutrimentsInformationMap, RecipeEntity.ID_NUTRIMENT_CARBOHYDRATE);
		NutrimentInformation fat = getNutrimentInformation(nutrimentsInformationMap, RecipeEntity.ID_NUTRIMENT_FAT);
		NutrimentInformation protein = getNutrimentInformation(nutrimentsInformationMap, RecipeEntity.ID_NUTRIMENT_PROTEIN);
		NutrimentInformation fiber = getNutrimentInformation(nutrimentsInformationMap, RecipeEntity.ID_NUTRIMENT_FIBER);
		MacronutrimentsSummaryStatisticsDTO summary = MacronutrimentsSummaryStatisticsDTO.builder()
				.energy((int) energy.getQuantityGrams())
				.carbohydrate((int) carbohydrate.getQuantityGrams())
				.fat((int) fat.getQuantityGrams())
				.protein((int) protein.getQuantityGrams())
				.fiber((int) fiber.getQuantityGrams())
				.build();
		MacronutrimentsDistributionStatisticsDTO distribution = MacronutrimentsDistributionStatisticsDTO.builder()
				.fat(toDistribution("Fat", fat))
				.protein(toDistribution("Protein", protein))
				.carbohydrate(toDistribution("Carbohydrate", carbohydrate))
				.build();
		return MacronutrimentsStatisticsDTO.builder()
				.idRecipe(idRecipe)
				.macronutrimentsSummaryStatistics(summary)
				.macronutrimentsDistributionStatistics(distribution)
				.build();
	}

	@Transactional(readOnly = true)
	public List<CookingMethodDTO> getCookingMethods(Long idRecipe) {
		return getRecipe(idRecipe).getCookingMethods().stream()
				.map(CookingMethodMapper.MAPPER::convertToDto)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public void updateCookingMethod(Long idRecipe, Long idCookingMethod, UpdateCookingMethodParametersDTO updateCookingMethodParametersDTO) {
		getRecipe(idRecipe).updateCookingMethod(idCookingMethod, updateCookingMethodParametersDTO.getName());
	}

	public void createCookingMethod(Long idRecipe, CreateCookingMethodParametersDTO createCookingMethodParametersDTO) {
		getRecipe(idRecipe).addCookingMethod(createCookingMethodParametersDTO.getName());
	}

	public void deleteCookingMethod(Long idRecipe, Long idCookingMethod) {
		getRecipe(idRecipe).removeCookingMethod(idCookingMethod);
	}

	public void moveCookingMethod(Long idRecipe, Long idCookingMethod, int orderItem) {
		getRecipe(idRecipe).moveCookingMethod(idCookingMethod, orderItem);
	}

	public void createCommonMeasure(Long idRecipe, CreateRecipeCommonMeasureParametersDTO parameters) {
		getRecipe(idRecipe).addCommonMeasure(parameters.getSingularName(), parameters.getPluralName(), parameters.getQuantity(),
				parameters.getTotalGrams(), parameters.getEdiblePortion());
	}

	@Transactional(readOnly = true)
	public List<RecipeCommonMeasureDTO> getCommonMeasures(Long idRecipe) {
		return getRecipe(idRecipe).getCommonMeasures().stream()
				.map(RecipeCommonMeasureMapper.MAPPER::convertToDomain)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public void deleteCommonMeasure(Long idRecipe, Long idCommonMeasure) {
		getRecipe(idRecipe).removeCommonMeasure(idCommonMeasure);
	}

	public void updateCommonMeasure(Long idRecipe, Long idCommonMeasure, UpdateRecipeCommonMeasureParametersDTO parameters) {
		getRecipe(idRecipe).updateCommonMeasure(idCommonMeasure, parameters.getSingularName(), parameters.getPluralName(),
				parameters.getQuantity(), parameters.getTotalGrams(), parameters.getEdiblePortion());
	}

	private RecipeEntity getRecipe(Long id) {
		return recipeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(RecipeEntity.class, "id", String.valueOf(id)));
	}

	private RecipeIngredientEntity getRecipeIngredient(Long id) {
		return recipeIngredientRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(RecipeIngredientEntity.class, "id", String.valueOf(id)));
	}

	private RecipeUserEntity buildRecipeUser(RecipeEntity recipeEntity, String username) {
		return RecipeUserEntity.builder()
				.recipeEntity(recipeEntity)
				.recipeUserPkEntity(RecipeUserPkEntity.builder().username(username).build())
				.build();
	}

	private void validateRecipeUser(RecipeEntity recipeEntity, String username) {
		boolean owner = recipeEntity.getRecipeUserEntity()
				.stream()
				.anyMatch(user -> user.getRecipeUserPkEntity().getUsername().equals(username));
		if (!owner) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The user has not permission to manage the recipe");
		}
	}

	private Optional<IngredientFoodEntity> findIngredientFood(RecipeIngredientEntity recipeIngredientEntity, Long idIngredientFood) {
		return recipeIngredientEntity.getIngredientFoodEntities().stream()
				.filter(ingredientFoodEntity -> Objects.equals(idIngredientFood, ingredientFoodEntity.getId()))
				.findFirst();
	}

	private RecipeIngredientEntity getRecipeIngredientEntity(RecipeIngredientParametersDTO recipeIngredientParametersDTO, RecipeEntity recipeEntity) {
		return recipeIngredientParametersDTO.getIdRecipeIngredient()
				.map(this::getRecipeIngredient)
				.orElseGet(() -> RecipeIngredientEntity.builder()
						.recipeEntity(recipeEntity)
						.ingredientFoodEntities(new ArrayList<>())
						.build());
	}

	private List<NutritionalInformationDTO> getNutritionalInformations(RecipeEntity recipe) {
		Map<Long, Optional<NutrimentInformation>> nutrimentsPer100 = recipe.getNutrimentsInformation(GRAMS_100);
		Map<Long, Optional<NutrimentInformation>> nutrimentsPer120 = recipe.getNutrimentsInformation(GRAMS_120);
		List<CatalogDetailDTO> macronutriments = catalogBusinessService.getCatalog(ID_CATALOG_MACRONUTRIMENTS).getCatalogDetails();
		List<CatalogDetailDTO> micronutriments = catalogBusinessService.getCatalog(ID_CATALOG_MICRONUTRIMENTS).getCatalogDetails()
				.stream()
				.filter(nutriment -> REPORT_MICRONUTRIMENTS.contains(nutriment.getName()))
				.collect(Collectors.toList());
		List<NutritionalInformationDTO> nutritionalInformations = new ArrayList<>();
		addNutritionalInfo(macronutriments, nutritionalInformations, nutrimentsPer100, nutrimentsPer120);
		addNutritionalInfo(micronutriments, nutritionalInformations, nutrimentsPer100, nutrimentsPer120);
		return nutritionalInformations;
	}

	/**
	 * Grams of a weight unit, read from the links of the unit in the weight catalog.
	 */
	private double getQuantityGrams(Long idUnit) {
		if (idUnit == null) {
			return 1d;
		}
		return catalogBusinessService.getCatalogDetail(idUnit).getCatalogLinks()
				.stream()
				.map(catalogLink -> catalogLink.getCatalogDetail().getLabel())
				.map(Double::parseDouble)
				.findAny()
				.orElse(1d);
	}

	private NutrimentInformation getNutrimentInformation(Map<Long, Optional<NutrimentInformation>> nutrimentsInformation, long idNutriment) {
		return nutrimentsInformation
				.getOrDefault(idNutriment, Optional.empty())
				.orElseGet(() -> NutrimentInformation.builder().idNutriment(idNutriment).build());
	}

	private MacronutrimentsDistributionDTO toDistribution(String name, NutrimentInformation nutrimentInformation) {
		return MacronutrimentsDistributionDTO.builder()
				.name(name)
				.grams(nutrimentInformation.getQuantityGrams())
				.kcal(nutrimentInformation.getKcal())
				.percentageValue(nutrimentInformation.getPercentageValue())
				.build();
	}

	private void setRecipeNutriments(RecipeEntity recipeEntity, IngredientFoodEntity ingredientFoodEntity) {
		recipeEntity.addNutriments(ingredientFoodEntity.getNutriments(), ingredientFoodEntity.getQuantity(), getTotalGrams(ingredientFoodEntity));
	}

	private void removeRecipeNutriment(RecipeEntity recipeEntity, IngredientFoodEntity ingredientFoodEntity) {
		recipeEntity.removeNutriments(ingredientFoodEntity.getNutriments(), ingredientFoodEntity.getQuantity(), getTotalGrams(ingredientFoodEntity));
	}

	private double getTotalGrams(IngredientFoodEntity ingredientFoodEntity) {
		return ingredientFoodEntity.getIngredientCommonMeasureEntity() != null
				? ingredientFoodEntity.getIngredientCommonMeasureEntity().getTotalGrams()
				: 1;
	}

	private void addNutritionalInfo(List<CatalogDetailDTO> nutriments, List<NutritionalInformationDTO> nutritionalInformation,
			Map<Long, Optional<NutrimentInformation>> nutrimentsPer100, Map<Long, Optional<NutrimentInformation>> nutrimentsPer120) {
		nutritionalInformation.addAll(nutriments
				.stream()
				.map(nutriment -> NutritionalInformationDTO.builder()
						.label(nutriment.getLabel())
						.nutrimentsPer100(getValue(nutrimentsPer100, nutriment.getId(), information -> formatQuantity(nutriment, information)))
						.nutrimentsPer120(getValue(nutrimentsPer120, nutriment.getId(), information -> formatQuantity(nutriment, information)))
						.percentageValue(getCatalogLinkLabel(nutriment, DRI_CATALOG_TYPE, " %", "--"))
						.build())
				.collect(Collectors.toList()));
	}

	private String formatQuantity(CatalogDetailDTO nutriment, NutrimentInformation information) {
		return information.getQuantityGrams() + " "
				+ getCatalogLinkLabel(nutriment, PREFIX_CATALOG_TYPE, "", "")
				+ getCatalogLinkLabel(nutriment, UNIT_CATALOG_TYPE, "", "");
	}

	private String getCatalogLinkLabel(CatalogDetailDTO catalog, String catalogLinkType, String suffix, String defaultValue) {
		if (catalog.getCatalogLinks() == null) {
			return defaultValue;
		}
		return catalog.getCatalogLinks()
				.stream()
				.filter(catalogLink -> catalogLink.getLinkType().equalsIgnoreCase(catalogLinkType))
				.map(catalogLink -> catalogLink.getCatalogDetail().getLabel() + suffix)
				.findFirst()
				.orElse(defaultValue);
	}

	private String getValue(Map<Long, Optional<NutrimentInformation>> nutrimentsMap, Long catalogId,
			Function<NutrimentInformation, String> mapper) {
		return nutrimentsMap.getOrDefault(catalogId, Optional.empty())
				.map(mapper)
				.orElse("");
	}
}
