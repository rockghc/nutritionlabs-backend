package com.nutritionlabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * NutritionLabs backend: users, catalogs, foods, recipes, files and reports.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class NutritionLabsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutritionLabsApplication.class, args);
	}
}
