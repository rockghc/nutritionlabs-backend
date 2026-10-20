package com.nutritionlabs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.nutritionlabs.config.KeycloakJwtAuthenticationConverter;

/**
 * Calls the API against the NutritionLabsBD database with the migrated data. Every test is rolled back.
 *
 * <p>Run with {@code mvn verify -Dgroups=integration}.
 */
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest {

	private static final String USERNAME = "user1";

	@Autowired
	private MockMvc mockMvc;

	private static JwtRequestPostProcessor keycloakUser(String username, String role) {
		return jwt()
				.jwt(token -> token
						.claim("preferred_username", username)
						.claim("realm_access", Map.of("roles", List.of(role))))
				.authorities(token -> new ArrayList<GrantedAuthority>(new KeycloakJwtAuthenticationConverter().convert(token).getAuthorities()));
	}

	private static String idOf(String json) {
		return String.valueOf((Object) JsonPath.read(json, "$.id"));
	}

	private static JwtRequestPostProcessor user1() {
		return keycloakUser(USERNAME, "user");
	}

	@Test
	void requiresToken() throws Exception {
		mockMvc.perform(get("/resourceUser/user/")).andExpect(status().isUnauthorized());
	}

	@Test
	void requiresUserRole() throws Exception {
		mockMvc.perform(get("/resourceUser/user/").with(keycloakUser(USERNAME, "other"))).andExpect(status().isForbidden());
	}

	@Test
	void listsPatientsOfUserWithTrailingSlash() throws Exception {
		mockMvc.perform(get("/resourceUser/user/").with(user1()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name").exists());
	}

	@Test
	void readsPatientHistoriesAndCatalogs() throws Exception {
		mockMvc.perform(get("/resourceUser/user/1").with(user1())).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
		mockMvc.perform(get("/resourceUser/user/1/dietaryhistory/").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/1/personalhistory/").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/1/medicalhistory/diseases").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/1/appointmentinformation/clinicalgoals").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/clinicalgoals").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(80)));
		mockMvc.perform(get("/resourceUser/user/workplace").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/1/observations/?page=0&size=5").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.totalElements").exists());
		mockMvc.perform(get("/resourceUser/tag/?iduser=1").with(user1())).andExpect(status().isOk());
	}

	@Test
	void updatesPatientTimeProperty() throws Exception {
		String historyId = mockMvc.perform(get("/resourceUser/user/1/dietaryhistory").with(user1()))
				.andReturn().getResponse().getContentAsString().transform(ApiIntegrationTest::idOf);
		mockMvc.perform(patch("/resourceUser/user/dietaryhistory").with(user1())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"type":"com.nutritionlabs.domain.users.entity.DietaryHistoryEntity","id":%s,"propertyName":"usualWakeTime","value":"07:15:00"}
						""".formatted(historyId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.usualWakeTime").value("07:15:00"));
	}

	@Test
	void createsPatientWithHistories() throws Exception {
		String response = mockMvc.perform(post("/resourceUser/user/").with(user1())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name":"Integration test","birthdate":"1990-05-17","workplace":{"id":1}}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idWorkplace").value(1))
				.andReturn().getResponse().getContentAsString();
		String id = idOf(response);
		mockMvc.perform(get("/resourceUser/user/" + id + "/appointmentinformation").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
		mockMvc.perform(post("/resourceUser/user/" + id + "/dietaryhistory/allergy").with(user1())
				.contentType(MediaType.APPLICATION_JSON).content("{\"idAllergy\":1}"))
				.andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/" + id + "/dietaryhistory/allergies").with(user1()))
				.andExpect(jsonPath("$", hasSize(1)));
		mockMvc.perform(delete("/resourceUser/user/" + id + "/dietaryhistory/allergy/1").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceUser/user/" + id + "/dietaryhistory/allergies").with(user1()))
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void readsCatalogsWithLinks() throws Exception {
		mockMvc.perform(get("/resourceCatalog/catalog/").with(user1())).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(19)));
		mockMvc.perform(get("/resourceCatalog/catalog/1").with(user1()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.catalogDetails.length()").value(greaterThan(0)));
	}

	@Test
	void listsFoodsWithNutrimentsAndSorts() throws Exception {
		mockMvc.perform(get("/resourceFood/food?page=0&size=5&sort=name,asc&withCommonMeasures=true").with(user1()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0].energy").exists());
		mockMvc.perform(get("/resourceFood/food?page=0&size=5&sort=nutriment_10,desc").with(user1()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].micronutrientQuantity").exists());
		mockMvc.perform(get("/resourceFood/supplement?page=0&size=5").with(user1())).andExpect(status().isOk());
	}

	@Test
	void rejectsSqlInSortProperty() throws Exception {
		mockMvc.perform(get("/resourceFood/food?sort=name;drop table x").with(user1())).andExpect(status().isBadRequest());
	}

	@Test
	void readsRecipeData() throws Exception {
		mockMvc.perform(get("/resourceRecipe/recipe?page=0&size=10").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(greaterThan(0)));
		mockMvc.perform(get("/resourceRecipe/recipe/1").with(user1())).andExpect(status().isOk()).andExpect(jsonPath("$.name").exists());
		mockMvc.perform(get("/resourceRecipe/recipe/1/ingredients").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceRecipe/recipe/1/cookingmethods").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceRecipe/recipe/1/commonmeasures").with(user1())).andExpect(status().isOk());
		mockMvc.perform(get("/resourceRecipe/recipe/1/statistics").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.macronutrimentsSummaryStatistics").exists());
		mockMvc.perform(get("/resourceRecipe/recipe/1:report").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.nutritionalInformations").isArray());
		mockMvc.perform(get("/resourceRecipe/tag/?idrecipe=1").with(user1())).andExpect(status().isOk());
	}

	@Test
	void editsRecipeIngredientsAndDuplicates() throws Exception {
		String recipe = mockMvc.perform(post("/resourceRecipe/recipe").with(user1()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		String idRecipe = idOf(recipe);
		String idIngredient = mockMvc.perform(post("/resourceRecipe/recipe/" + idRecipe + "/ingredients/").with(user1())
				.contentType(MediaType.APPLICATION_JSON).content("{\"idFood\":1,\"quantity\":150}"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		mockMvc.perform(get("/resourceRecipe/recipe/" + idRecipe + "/ingredients").with(user1()))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].ingredientFoods[0].name", containsString("150.0 grams of")));
		mockMvc.perform(post("/resourceRecipe/recipe/" + idRecipe + "/cookingmethods/").with(user1())
				.contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Mix\"}")).andExpect(status().isOk());
		mockMvc.perform(patch("/resourceRecipe/recipe").with(user1())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"type\":\"com.nutritionlabs.domain.recipes.entity.RecipeEntity\",\"id\":" + idRecipe
						+ ",\"propertyName\":\"preparationTime\",\"value\":\"00:45\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.preparationTime").value("00:45:00"));
		mockMvc.perform(post("/resourceRecipe/recipe/" + idRecipe + ":duplicate").with(user1()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("New recipe (Copy)"));
		mockMvc.perform(delete("/resourceRecipe/recipe/" + idRecipe + "/ingredients/" + idIngredient).with(user1()))
				.andExpect(status().isOk());
		mockMvc.perform(delete("/resourceRecipe/recipe/" + idRecipe).with(user1())).andExpect(status().isOk());
	}

	@Test
	void forbidsDeletingRecipeOfOtherUser() throws Exception {
		mockMvc.perform(delete("/resourceRecipe/recipe/1").with(keycloakUser("someone-else", "user"))).andExpect(status().isForbidden());
	}

	@Test
	void servesImages() throws Exception {
		mockMvc.perform(get("/resourceFile/api/show/500/500?name=081025_10122022_63982.jpg").with(user1()))
				.andExpect(status().isOk());
		mockMvc.perform(get("/resourceFile/api/show?name=missing.jpg").with(user1()))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.IMAGE_JPEG));
	}

	@Test
	void generatesRecipePdf() throws Exception {
		byte[] pdf = mockMvc.perform(get("/resourceReport/report/recipe/1").with(user1()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_PDF))
				.andReturn().getResponse().getContentAsByteArray();
		assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");
	}
}
