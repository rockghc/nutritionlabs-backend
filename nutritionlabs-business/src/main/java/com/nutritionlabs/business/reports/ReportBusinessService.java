package com.nutritionlabs.business.reports;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.business.recipes.RecipeBusinessService;
import com.nutritionlabs.domain.recipes.dto.RecipeReportDTO;
import com.nutritionlabs.domain.reports.dto.RecipeReportDataDTO;

import net.sf.jasperreports.engine.JRException;

@Service
public class ReportBusinessService {

	private static final String RECIPE_REPORT_FOLDER = "reports/recipe/";
	private static final String RECIPE_REPORT = "RecipeReport.jasper";

	@Autowired
	private ReportGenerator reportGenerator;

	@Autowired
	private RecipeBusinessService recipeBusinessService;

	/**
	 * PDF with the ingredients, cooking methods and nutritional information of a recipe.
	 */
	@Transactional(readOnly = true)
	public byte[] getRecipeReport(Long idRecipe) throws JRException {
		RecipeReportDTO recipeReportDTO = recipeBusinessService.getRecipeReport(idRecipe);
		RecipeReportDataDTO recipeReportData = new RecipeReportDataDTO(recipeReportDTO, recipeReportDTO.getImage());
		Map<String, Object> parameters = Map.of("recipe", String.valueOf(recipeReportDTO.getName()));
		return reportGenerator.generatePdf(RECIPE_REPORT_FOLDER, RECIPE_REPORT, parameters, List.of(recipeReportData));
	}
}
