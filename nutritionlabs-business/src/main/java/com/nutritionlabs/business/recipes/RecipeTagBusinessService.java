package com.nutritionlabs.business.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nutritionlabs.dataaccess.recipes.RecipeRepository;
import com.nutritionlabs.dataaccess.recipes.RecipeTagRepository;
import com.nutritionlabs.domain.recipes.dto.RecipeTagCreationParamsDTO;
import com.nutritionlabs.domain.recipes.entity.RecipeEntity;
import com.nutritionlabs.domain.recipes.entity.RecipeTagEntity;
import com.nutritionlabs.mappers.recipes.RecipeTagParametersMapper;
import com.nutritionlabs.properties.PropertiesReader;

/**
 * Tags (categories) of the recipes.
 */
@Service
@Transactional(readOnly = true)
public class RecipeTagBusinessService {

	@Autowired
	private RecipeTagRepository recipeTagRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private PropertiesReader propertiesReader;

	public List<RecipeTagEntity> getAllTags(String username, Optional<Long> idRecipe) {
		if (idRecipe.isPresent()) {
			return recipeTagRepository.getByRecipeEntities_IdAndRecipeEntities_RecipeUserEntity_RecipeUserPkEntity_UsernameOrderByName(idRecipe.get(), username);
		}
		return recipeTagRepository.getDistinctByRecipeEntities_RecipeUserEntity_RecipeUserPkEntity_UsernameOrderByName(username);
	}

	/**
	 * Adds a tag to a recipe. An existing tag is used when the id is given, otherwise the tag is created.
	 */
	@Transactional
	public void createTag(String username, RecipeTagCreationParamsDTO tagCreationParamsDTO) {
		RecipeEntity recipeEntity = getRecipe(tagCreationParamsDTO.getIdRecipe());
		validateAccess(recipeEntity, username);
		RecipeTagEntity recipeTagEntity = tagCreationParamsDTO.getId() != null
				? recipeTagRepository.findById(tagCreationParamsDTO.getId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"))
				: recipeTagRepository.save(RecipeTagParametersMapper.MAPPER.tagParametersToRecipeTagEntity(tagCreationParamsDTO));
		if (recipeEntity.getRecipeTagsEntities() == null) {
			recipeEntity.setRecipeTagsEntities(new ArrayList<>());
		}
		if (!recipeEntity.getRecipeTagsEntities().contains(recipeTagEntity)) {
			recipeEntity.getRecipeTagsEntities().add(recipeTagEntity);
		}
		recipeRepository.save(recipeEntity);
	}

	/**
	 * Removes a tag from a recipe. The tag is deleted when no other recipe uses it.
	 */
	@Transactional
	public void deleteTag(String username, Long idTag, Long idRecipe) {
		RecipeTagEntity recipeTagEntity = recipeTagRepository.findById(idTag)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
		List<RecipeEntity> recipeEntities = recipeTagEntity.getRecipeEntities();
		if (recipeEntities == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, propertiesReader.getProperty("tag.recipeNotFound"));
		}
		RecipeEntity recipeEntity = getRecipe(idRecipe);
		validateAccess(recipeEntity, username);
		recipeEntity.getRecipeTagsEntities().remove(recipeTagEntity);
		if (recipeEntities.size() <= 1) {
			recipeRepository.flush();
			recipeTagRepository.delete(recipeTagEntity);
		}
	}

	private RecipeEntity getRecipe(Long idRecipe) {
		return recipeRepository.findById(idRecipe)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, propertiesReader.getProperty("tag.recipeNotFound")));
	}

	private void validateAccess(RecipeEntity recipeEntity, String username) {
		recipeEntity.getRecipeUserEntity()
				.stream()
				.filter(user -> user.getRecipeUserPkEntity().getUsername().equals(username))
				.findAny()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, propertiesReader.getProperty("tag.addTagDenied")));
	}
}
