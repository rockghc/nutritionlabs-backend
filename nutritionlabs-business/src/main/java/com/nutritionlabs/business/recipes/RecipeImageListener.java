package com.nutritionlabs.business.recipes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nutritionlabs.domain.common.EntityTypes;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;
import com.nutritionlabs.domain.files.event.ImageReplacedEvent;

/**
 * Stores the name of the uploaded image in the recipe property it was uploaded for, once the image is
 * committed. A failure here does not undo the upload.
 */
@Component
public class RecipeImageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecipeImageListener.class);

	@Autowired
	private RecipeBusinessService recipeBusinessService;

	@TransactionalEventListener(fallbackExecution = true)
	public void onImageReplaced(ImageReplacedEvent event) {
		if (!EntityTypes.RECIPE.equals(event.typeEntity())) {
			return;
		}
		try {
			recipeBusinessService.updateRecipeInNewTransaction(SingleValueDTO.<String>builder()
					.type(event.typeEntity())
					.id(event.idEntity())
					.propertyName(event.property())
					.value(event.fileName())
					.build());
		} catch (RuntimeException e) {
			LOGGER.error("Error setting the image {} on property {} of recipe {}", event.fileName(), event.property(), event.idEntity(), e);
		}
	}
}
