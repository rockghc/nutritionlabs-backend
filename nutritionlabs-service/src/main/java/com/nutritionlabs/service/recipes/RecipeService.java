package com.nutritionlabs.service.recipes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.recipes.RecipeBusinessService;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.recipes.dto.CookingMethodDTO;
import com.nutritionlabs.domain.recipes.dto.CreateCookingMethodParametersDTO;
import com.nutritionlabs.domain.recipes.dto.CreateRecipeCommonMeasureParametersDTO;
import com.nutritionlabs.domain.recipes.dto.MacronutrimentsStatisticsDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeCommonMeasureDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeIngredientDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeIngredientParametersDTO;
import com.nutritionlabs.domain.recipes.dto.RecipeReportDTO;
import com.nutritionlabs.domain.recipes.dto.RecipesPaginatedByUserParametersDTO;
import com.nutritionlabs.domain.recipes.dto.UpdateCookingMethodParametersDTO;
import com.nutritionlabs.domain.recipes.dto.UpdateRecipeCommonMeasureParametersDTO;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Recipes, their ingredients, cooking methods and common measures.
 */
@RestController
@RequestMapping(ResourcePaths.RECIPE + "/recipe")
public class RecipeService {

	@Autowired
	private RecipeBusinessService recipeBusinessService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping
	public Page<RecipeDTO> getRecipesPaginatedByUser(Pageable pageable, Principal principal, RecipeRestServiceParametersDTO parameters) {
		return recipeBusinessService.getRecipeEntities(RecipesPaginatedByUserParametersDTO.builder()
				.pageable(pageable)
				.username(securityUtils.getUsername(principal))
				.searchName(Optional.ofNullable(parameters.getSearchName()))
				.category(Optional.ofNullable(parameters.getCategory()))
				.build());
	}

	@GetMapping("/{id}")
	public RecipeDTO getRecipe(@PathVariable Long id) {
		return recipeBusinessService.getRecipeEntity(id);
	}

	@GetMapping("/{id}:report")
	public RecipeReportDTO getRecipeReport(@PathVariable Long id) {
		return recipeBusinessService.getRecipeReport(id);
	}

	@PostMapping
	public RecipeDTO createRecipe(Principal principal) {
		return recipeBusinessService.createRecipeEntity(securityUtils.getUsername(principal));
	}

	@PatchMapping
	public RecipeDTO updateRecipeAttribute(@RequestBody SingleValueDTO<?> singleValueDTO) {
		return recipeBusinessService.updateRecipe(singleValueDTO);
	}

	@DeleteMapping("/{idrecipe}")
	public void deleteRecipe(@PathVariable("idrecipe") Long idRecipe, Principal principal) {
		recipeBusinessService.deleteRecipe(idRecipe, securityUtils.getUsername(principal));
	}

	@PostMapping("/{idrecipe}:duplicate")
	public RecipeDTO duplicateRecipe(@PathVariable("idrecipe") Long idRecipe, Principal principal) {
		return recipeBusinessService.duplicateRecipe(idRecipe, securityUtils.getUsername(principal));
	}

	// Ingredients

	@GetMapping("/{id}/ingredients")
	public List<RecipeIngredientDTO> getRecipeIngredients(@PathVariable Long id) {
		return recipeBusinessService.getRecipeIngredients(id);
	}

	@PostMapping({ "/{idrecipe}/ingredients", "/{idrecipe}/ingredients/{idrecipeingredient}" })
	public String createRecipeIngredient(@PathVariable("idrecipe") Long idRecipe,
			@PathVariable(name = "idrecipeingredient", required = false) Long idRecipeIngredient,
			@RequestBody CreateRecipeIngredientParametersDTO parameters) {
		return recipeBusinessService.createRecipeIngredient(RecipeIngredientParametersDTO.builder()
				.idRecipe(idRecipe)
				.idRecipeIngredient(Optional.ofNullable(idRecipeIngredient))
				.idFood(parameters.getIdFood())
				.idUnit(Optional.ofNullable(parameters.getIdUnit()))
				.quantity(parameters.getQuantity())
				.build());
	}

	@DeleteMapping({ "/{idrecipe}/ingredients/{idrecipeingredient}", "/{idrecipe}/ingredients/{idrecipeingredient}/{idingredientfood}" })
	public void deleteRecipeIngredient(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idrecipeingredient") Long idRecipeIngredient,
			@PathVariable(name = "idingredientfood", required = false) Long idIngredientFood) {
		recipeBusinessService.deleteRecipeIngredient(idRecipe, idRecipeIngredient, Optional.ofNullable(idIngredientFood));
	}

	@PutMapping("/{idrecipe}/ingredients/{idrecipeingredient}/{idingredientfood}:updatequantity")
	public void updateRecipeIngredientQuantity(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idrecipeingredient") Long idRecipeIngredient,
			@PathVariable("idingredientfood") Long idIngredientFood, @RequestBody UpdateRecipeIngredientQuantityParametersDTO parameters) {
		recipeBusinessService.updateRecipeIngredientQuantity(RecipeIngredientParametersDTO.builder()
				.idRecipe(idRecipe)
				.idRecipeIngredient(Optional.of(idRecipeIngredient))
				.idIngredientFood(idIngredientFood)
				.idUnit(Optional.ofNullable(parameters.getIdUnit()))
				.quantity(parameters.getQuantity())
				.build());
	}

	@PutMapping("/{idrecipe}/ingredients/{idrecipeingredient}/{idingredientfood}:updatename")
	public void updateRecipeIngredientName(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idrecipeingredient") Long idRecipeIngredient,
			@PathVariable("idingredientfood") Long idIngredientFood, @RequestBody UpdateRecipeIngredientNameParametersDTO parameters) {
		recipeBusinessService.updateRecipeIngredientName(RecipeIngredientParametersDTO.builder()
				.idRecipe(idRecipe)
				.idRecipeIngredient(Optional.of(idRecipeIngredient))
				.idIngredientFood(idIngredientFood)
				.name(parameters.getName())
				.build());
	}

	@GetMapping("/{idrecipe}/statistics")
	public MacronutrimentsStatisticsDTO getMacronutrimentsStatistics(@PathVariable("idrecipe") Long idRecipe) {
		return recipeBusinessService.getMacronutrimentsStatistics(idRecipe);
	}

	// Cooking methods

	@GetMapping("/{idrecipe}/cookingmethods")
	public List<CookingMethodDTO> getCookingMethods(@PathVariable("idrecipe") Long idRecipe) {
		return recipeBusinessService.getCookingMethods(idRecipe);
	}

	@PostMapping("/{idrecipe}/cookingmethods")
	public void createCookingMethod(@PathVariable("idrecipe") Long idRecipe, @RequestBody CookingMethodRestParametersDTO parameters) {
		recipeBusinessService.createCookingMethod(idRecipe, CreateCookingMethodParametersDTO.builder().name(parameters.getName()).build());
	}

	@PutMapping("/{idrecipe}/cookingmethods/{idcookingmethod}")
	public void updateCookingMethod(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idcookingmethod") Long idCookingMethod,
			@RequestBody CookingMethodRestParametersDTO parameters) {
		recipeBusinessService.updateCookingMethod(idRecipe, idCookingMethod, UpdateCookingMethodParametersDTO.builder().name(parameters.getName()).build());
	}

	@PutMapping("/{idrecipe}/cookingmethods/{idcookingmethod}:moveitem")
	public void moveCookingMethod(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idcookingmethod") Long idCookingMethod,
			@RequestBody MoveCookingMethodRestParametersDTO parameters) {
		recipeBusinessService.moveCookingMethod(idRecipe, idCookingMethod, parameters.getOrderItem());
	}

	@DeleteMapping("/{idrecipe}/cookingmethods/{idcookingmethod}")
	public void deleteCookingMethod(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idcookingmethod") Long idCookingMethod) {
		recipeBusinessService.deleteCookingMethod(idRecipe, idCookingMethod);
	}

	// Common measures

	@GetMapping("/{idrecipe}/commonmeasures")
	public List<RecipeCommonMeasureDTO> getCommonMeasures(@PathVariable("idrecipe") Long idRecipe) {
		return recipeBusinessService.getCommonMeasures(idRecipe);
	}

	@PostMapping("/{idrecipe}/commonmeasures")
	public void createCommonMeasure(@PathVariable("idrecipe") Long idRecipe, @RequestBody RecipeCommonMeasureRestParametersDTO parameters) {
		recipeBusinessService.createCommonMeasure(idRecipe, CreateRecipeCommonMeasureParametersDTO.builder()
				.singularName(parameters.getSingularName())
				.pluralName(parameters.getPluralName())
				.quantity(parameters.getQuantity())
				.totalGrams(parameters.getTotalGrams())
				.ediblePortion(parameters.getEdiblePortion())
				.build());
	}

	@PutMapping("/{idrecipe}/commonmeasures/{idcommonmeasure}")
	public void updateCommonMeasure(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idcommonmeasure") Long idCommonMeasure,
			@RequestBody RecipeCommonMeasureRestParametersDTO parameters) {
		recipeBusinessService.updateCommonMeasure(idRecipe, idCommonMeasure, UpdateRecipeCommonMeasureParametersDTO.builder()
				.singularName(parameters.getSingularName())
				.pluralName(parameters.getPluralName())
				.quantity(parameters.getQuantity())
				.totalGrams(parameters.getTotalGrams())
				.ediblePortion(parameters.getEdiblePortion())
				.build());
	}

	@DeleteMapping("/{idrecipe}/commonmeasures/{idcommonmeasure}")
	public void deleteCommonMeasure(@PathVariable("idrecipe") Long idRecipe, @PathVariable("idcommonmeasure") Long idCommonMeasure) {
		recipeBusinessService.deleteCommonMeasure(idRecipe, idCommonMeasure);
	}
}
