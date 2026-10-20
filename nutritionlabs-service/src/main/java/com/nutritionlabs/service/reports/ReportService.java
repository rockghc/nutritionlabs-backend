package com.nutritionlabs.service.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutritionlabs.business.reports.ReportBusinessService;
import com.nutritionlabs.service.ResourcePaths;

import net.sf.jasperreports.engine.JRException;

/**
 * PDF reports.
 */
@RestController
@RequestMapping(ResourcePaths.REPORT + "/report")
public class ReportService {

	@Autowired
	private ReportBusinessService reportBusinessService;

	@GetMapping("/recipe/{idrecipe}")
	public ResponseEntity<byte[]> getRecipeReport(@PathVariable("idrecipe") Long idRecipe) throws JRException {
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.body(reportBusinessService.getRecipeReport(idRecipe));
	}
}
