package com.nutritionlabs.domain.recipes.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nutritionlabs.domain.common.MathUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
@Entity
@Table(name = "nlabs_recipes")
public class RecipeEntity {

	public static final long ID_NUTRIMENT_ENERGY = 1;
	public static final long ID_NUTRIMENT_CARBOHYDRATE = 2;
	public static final long ID_NUTRIMENT_FAT = 3;
	public static final long ID_NUTRIMENT_PROTEIN = 4;
	public static final long ID_NUTRIMENT_FIBER = 10;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "preparation_time")
	private LocalTime preparationTime;

	@Column(name = "total_time")
	private LocalTime totalTime;

	@Column(name = "weight")
	private Double weight;

	@Column(name = "weight_unit")
	private Long weightUnit;

	@Column(name = "portions")
	private Long portions;

	@Builder.Default
	@OneToMany(mappedBy = "recipeEntity", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnoreProperties("recipeEntity")
	private List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();

	@Builder.Default
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinTable(name = "nlabs_recipe_tags", joinColumns = @JoinColumn(name = "id_recipe"), inverseJoinColumns = @JoinColumn(name = "id_tag"))
	private List<RecipeTagEntity> recipeTagsEntities = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "recipeEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<RecipeNutrimentEntity> nutriments = new ArrayList<>();

	@Column(name = "image_name")
	private String imageName;

	@OneToMany(orphanRemoval = true, mappedBy = "recipeEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	private List<RecipeUserEntity> recipeUserEntity;

	@Builder.Default
	@OneToMany(orphanRemoval = true, mappedBy = "recipeEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	private List<RecipeCookingMethodEntity> cookingMethods = new ArrayList<>();

	@Builder.Default
	@OneToMany(orphanRemoval = true, mappedBy = "recipeEntity", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
	private List<RecipeCommonMeasureEntity> commonMeasures = new ArrayList<>();

	@Column(name = "is_private")
	private Boolean isPrivate;

	@Version
	private long version;

	@Getter
	@Builder
	public static class NutrimentInformation {
		private long idNutriment;
		private double quantityGrams;
		private double percentageValue;
		private double kcal;
	}

	public Map<Long, Optional<NutrimentInformation>> getNutrimentsInformation(double quantityGramsPortion) {
		if (this.getPortions() == null || quantityGramsPortion == 0) {
			return new HashMap<>();
		}
		double totalGrams = getTotalGrams();
		double totalGramsPortion = quantityGramsPortion / this.getPortions();
		Map<Long, Optional<Double>> nutrimentsGrams = getNutrimentsSummary()
				.stream()
				.collect(Collectors.groupingBy(RecipeNutrimentEntity::getIdNutrimentCatalog,
						Collectors.mapping(nutriment -> (nutriment.getQuantity() * totalGramsPortion) / totalGrams,
								Collectors.reducing((nutrimentInfo1, nutrimentInfo2) -> nutrimentInfo1))));
		double totalGramsSummary = nutrimentsGrams
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey() != ID_NUTRIMENT_ENERGY && entry.getKey() != ID_NUTRIMENT_FIBER)
				.map(Map.Entry::getValue)
				.filter(Optional::isPresent)
				.mapToDouble(Optional::get)
				.sum();
		double kcalTotal = nutrimentsGrams.getOrDefault(ID_NUTRIMENT_ENERGY, Optional.empty()).orElse(0d);
		return getNutrimentsSummary().stream().map(nutriment -> {
			double quantityGrams = nutrimentsGrams.getOrDefault(nutriment.getIdNutrimentCatalog(), Optional.empty()).orElse(0d);
			double percentageValue = (quantityGrams * 100) / totalGramsSummary;
			return NutrimentInformation.builder()
					.idNutriment(nutriment.getIdNutrimentCatalog())
					.quantityGrams(MathUtils.truncateDecimal(quantityGrams, 2))
					.percentageValue(MathUtils.truncateDecimal(percentageValue, 2))
					.kcal(MathUtils.truncateDecimal((kcalTotal * percentageValue) / 100, 2))
					.build();
		})
		.collect(Collectors.groupingBy(NutrimentInformation::getIdNutriment,
				Collectors.reducing((nutrimentInfo1, nutrimentInfo2) -> nutrimentInfo1)));
	}

	public void addNutriments(List<IngredientFoodNutrimentEntity> nutrimentsToAdd, double ingredientFoodQuantity, double totalGrams) {
		if (nutriments == null) {
			nutriments = new ArrayList<>();
		}
		if (nutrimentsToAdd != null) {
			for (IngredientFoodNutrimentEntity foodNutriment : nutrimentsToAdd) {
				Optional<RecipeNutrimentEntity> recipeNutrimentsPresent = nutriments
						.stream()
						.filter(recipeNutriment -> recipeNutriment.getIdNutrimentCatalog() == foodNutriment.getIdNutrimentCatalog())
						.findAny();
				double nutrimentQuantity = MathUtils.calculateNutrimentQuantity(foodNutriment.getQuantity(),
						ingredientFoodQuantity, MathUtils.FOOD_BASE_QUANTITY, totalGrams);
				if (recipeNutrimentsPresent.isPresent()) {
					RecipeNutrimentEntity recipeNutrimentEntity = recipeNutrimentsPresent.get();
					recipeNutrimentEntity.setQuantity(MathUtils.truncateDecimal(recipeNutrimentEntity.getQuantity() + nutrimentQuantity, 2));
				} else {
					RecipeNutrimentEntity recipeNutrimentEntity = RecipeNutrimentEntity.builder()
							.idNutrimentCatalog(foodNutriment.getIdNutrimentCatalog())
							.quantity(MathUtils.truncateDecimal(nutrimentQuantity, 2))
							.recipeEntity(this)
							.build();
					nutriments.add(recipeNutrimentEntity);
				}
			}
		}
	}

	public void removeNutriments(List<IngredientFoodNutrimentEntity> nutrimentsToRemove, double ingredientFoodQuantity, double totalGrams) {
		if (nutriments == null || nutrimentsToRemove == null) {
			return;
		}
		for (IngredientFoodNutrimentEntity foodNutriment : nutrimentsToRemove) {
			Optional<RecipeNutrimentEntity> recipeNutrimentsPresent = nutriments.stream()
					.filter(recipeNutriment -> recipeNutriment.getIdNutrimentCatalog() == foodNutriment.getIdNutrimentCatalog())
					.findAny();
			if (recipeNutrimentsPresent.isPresent()) {
				double nutrimentQuantity = MathUtils.calculateNutrimentQuantity(foodNutriment.getQuantity(),
						ingredientFoodQuantity, MathUtils.FOOD_BASE_QUANTITY, totalGrams);
				RecipeNutrimentEntity recipeNutrimentEntity = recipeNutrimentsPresent.get();
				recipeNutrimentEntity.setQuantity(MathUtils.truncateDecimal(recipeNutrimentEntity.getQuantity() - nutrimentQuantity, 2));
				if (Math.round(recipeNutrimentEntity.getQuantity()) == 0) {
					nutriments.remove(recipeNutrimentEntity);
				}
			}
		}
	}

	public List<RecipeNutrimentEntity> getNutrimentsSummary() {
		Predicate<RecipeNutrimentEntity> macronutrimentsPredicate = nutriment -> nutriment.getIdNutrimentCatalog() == ID_NUTRIMENT_ENERGY;
		macronutrimentsPredicate = macronutrimentsPredicate.or(nutriment -> nutriment.getIdNutrimentCatalog() == ID_NUTRIMENT_CARBOHYDRATE);
		macronutrimentsPredicate = macronutrimentsPredicate.or(nutriment -> nutriment.getIdNutrimentCatalog() == ID_NUTRIMENT_FAT);
		macronutrimentsPredicate = macronutrimentsPredicate.or(nutriment -> nutriment.getIdNutrimentCatalog() == ID_NUTRIMENT_PROTEIN);
		macronutrimentsPredicate = macronutrimentsPredicate.or(nutriment -> nutriment.getIdNutrimentCatalog() == ID_NUTRIMENT_FIBER);
		return this.nutriments.stream()
				.filter(macronutrimentsPredicate)
				.collect(Collectors.toList());
	}

	public double getTotalGrams() {
		return this.recipeIngredientEntities
				.stream()
				.mapToDouble(RecipeIngredientEntity::getTotalGrams)
				.sum();
	}

	public List<RecipeCookingMethodEntity> getCookingMethods() {
		return this.cookingMethods.stream()
				.sorted((cookingMethod1, cookingMethod2) -> cookingMethod1.getOrderItems().compareTo(cookingMethod2.getOrderItems()))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public void addCookingMethod(String name) {
		long lastCookingMethodOrder = this.cookingMethods
				.stream()
				.mapToLong(c -> c.getOrderItems() + 1)
				.max()
				.orElse(0);
		this.cookingMethods.add(RecipeCookingMethodEntity.builder()
				.name(name)
				.recipeEntity(this)
				.orderItems(lastCookingMethodOrder)
				.build());
	}

	public void addCommonMeasure(String singularName, String pluralName, Double quantity, Double totalGrams, Double ediblePortion) {
		this.commonMeasures.add(RecipeCommonMeasureEntity.builder()
				.recipeEntity(this)
				.singularName(singularName)
				.pluralName(pluralName)
				.quantity(quantity)
				.totalGrams(totalGrams)
				.ediblePortion(ediblePortion)
				.build());
	}

	public void updateCommonMeasure(Long idCommonMeasure, String singularName, String pluralName, Double quantity, Double totalGrams, Double ediblePortion) {
		this.commonMeasures
				.stream()
				.filter(c -> c.getId().equals(idCommonMeasure))
				.findFirst()
				.ifPresent(c -> c.updateCommonMeasure(singularName, pluralName, quantity, totalGrams, ediblePortion));
	}

	public void removeCommonMeasure(Long idCommonMeasure) {
		this.commonMeasures.removeIf(commonMeasure -> commonMeasure.getId().equals(idCommonMeasure));
	}

	public void removeCookingMethod(long idCookingMethod) {
		this.cookingMethods.removeIf(cookingMethod -> cookingMethod.getId().equals(idCookingMethod));
		updateOrderItems();
	}

	public void moveCookingMethod(long idCookingMethod, int orderItem) {
		RecipeCookingMethodEntity recipeCookingMethodEntity = this.cookingMethods.stream()
				.filter(cookingMethod -> cookingMethod.getId() == idCookingMethod)
				.findFirst()
				.orElseThrow();
		OptionalLong maxPosition = this.cookingMethods.stream().mapToLong(RecipeCookingMethodEntity::getOrderItems).max();
		if (orderItem == recipeCookingMethodEntity.getOrderItems()) {
			return;
		} else if (maxPosition.isEmpty() || orderItem > maxPosition.getAsLong() || orderItem < 0) {
			throw new IllegalArgumentException("Invalid order item index. min 0 max " + maxPosition.orElse(0L));
		}
		Predicate<RecipeCookingMethodEntity> p = c -> orderItem > c.getOrderItems();
		long fromIndex = p.test(recipeCookingMethodEntity) ? recipeCookingMethodEntity.getOrderItems() : orderItem;
		long toIndex = p.test(recipeCookingMethodEntity) ? orderItem : recipeCookingMethodEntity.getOrderItems();
		int direction = p.test(recipeCookingMethodEntity) ? -1 : 1;
		recipeCookingMethodEntity.setOrderItems((long) orderItem);
		for (RecipeCookingMethodEntity recipeCookingMethod : cookingMethods) {
			if (recipeCookingMethod.getId() != idCookingMethod
					&& (recipeCookingMethod.getOrderItems() >= fromIndex && recipeCookingMethod.getOrderItems() <= toIndex)) {
				recipeCookingMethod.setOrderItems(recipeCookingMethod.getOrderItems() + direction);
			}
		}
	}

	public void updateCookingMethod(Long idCookingMethod, String name) {
		this.cookingMethods
				.stream()
				.filter(cookingMethod -> cookingMethod.getId().equals(idCookingMethod))
				.findAny()
				.ifPresent(cookingMethod -> cookingMethod.setName(name));
	}

	private void updateOrderItems() {
		long orderItems = 0;
		Collections.sort(this.cookingMethods, (cookingMethod1, cookingMethod2) -> cookingMethod1.getOrderItems().compareTo(cookingMethod2.getOrderItems()));
		for (RecipeCookingMethodEntity cookingMethod : this.cookingMethods) {
			cookingMethod.setOrderItems(orderItems);
			orderItems++;
		}
	}

	/**
	 * Copies the plain attributes of the recipe. Relations are copied by the business service.
	 */
	public RecipeEntity clone() {
		return RecipeEntity.builder()
				.name(getName())
				.description(getDescription())
				.preparationTime(getPreparationTime())
				.totalTime(getTotalTime())
				.weight(getWeight())
				.weightUnit(getWeightUnit())
				.portions(getPortions())
				.isPrivate(getIsPrivate())
				.build();
	}
}
