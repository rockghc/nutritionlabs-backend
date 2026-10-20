package com.nutritionlabs.service.recipes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.recipes.RecipeTagBusinessService;
import com.nutritionlabs.domain.recipes.dto.RecipeTagCreationParamsDTO;
import com.nutritionlabs.domain.recipes.entity.RecipeTagEntity;
import com.nutritionlabs.service.ResourcePaths;
import com.nutritionlabs.service.utils.SecurityUtils;

/**
 * Tags (categories) of the recipes.
 */
@RestController
@RequestMapping(ResourcePaths.RECIPE + "/tag")
public class RecipeTagService {

	@Autowired
	private RecipeTagBusinessService recipeTagBusinessService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping
	public List<RecipeTagEntity> getAllTags(Principal principal, @RequestParam(name = "idrecipe", required = false) Optional<Long> idRecipe) {
		return recipeTagBusinessService.getAllTags(securityUtils.getUsername(principal), idRecipe);
	}

	@PostMapping
	public void createRecipeTag(Principal principal, @RequestBody RecipeTagCreationParamsDTO tagCreationParamsDTO) {
		recipeTagBusinessService.createTag(securityUtils.getUsername(principal), tagCreationParamsDTO);
	}

	@DeleteMapping("/{idTag}")
	public void deleteRecipeTag(Principal principal, @PathVariable Long idTag, @RequestParam(name = "idrecipe") Long idRecipe) {
		recipeTagBusinessService.deleteTag(securityUtils.getUsername(principal), idTag, idRecipe);
	}
}
