package com.nutritionlabs.dataaccess.recipes;

import static com.nutritionlabs.dataaccess.commons.NutrimentSortSupport.buildOrderItems;
import static com.nutritionlabs.dataaccess.commons.NutrimentSortSupport.isAdditionalProperty;
import static com.nutritionlabs.dataaccess.commons.NutrimentSortSupport.pageableContainsAdditionalPropertyToSort;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.nutritionlabs.dataaccess.commons.NutrimentSortSupport;
import com.nutritionlabs.domain.recipes.dto.RecipeDTO;
import com.nutritionlabs.domain.recipes.dto.RecipesPaginatedByUserParametersDTO;

/**
 * Lists the recipes of a user with their main nutriments (energy, carbohydrate, fat and protein).
 */
public class RecipeCustomRepositoryImpl implements RecipeCustomRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public RecipeCustomRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Page<RecipeDTO> findRecipesWithNutriments(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		Pageable pageable = recipeDomainParametersDTO.getPageable();
		if (pageable.isPaged()) {
			mapSqlParameterSource.addValue("pOffset", pageable.getOffset());
			mapSqlParameterSource.addValue("pLimit", pageable.getPageSize());
		}
		addFilterParameters(recipeDomainParametersDTO, mapSqlParameterSource);
		Optional<Sort.Order> additionalOrder = pageable.getSort().stream().findFirst().filter(order -> isAdditionalProperty(order));
		List<RecipeDTO> recipes = jdbcTemplate.query(buildSelectRecipesQuery(recipeDomainParametersDTO), mapSqlParameterSource, (rs, rowNum) -> {
			RecipeDTO.RecipeDTOBuilder recipeDtoBuilder = RecipeDTO.builder()
					.id(rs.getLong("id"))
					.name(rs.getString("name"))
					.description(rs.getString("description"))
					.preparationTime(rs.getObject("preparation_time", LocalTime.class))
					.totalTime(Optional.ofNullable(rs.getObject("total_time", LocalTime.class)))
					.imageName(rs.getString("image_name"))
					.weight(rs.getDouble("weight"))
					.portions(rs.getInt("portions"))
					.energy(rs.getDouble("nutriment_1"))
					.carbohydrate(rs.getDouble("nutriment_2"))
					.fat(rs.getDouble("nutriment_3"))
					.protein(rs.getDouble("nutriment_4"));
			if (additionalOrder.isPresent()) {
				recipeDtoBuilder.micronutrientQuantity(rs.getDouble(additionalOrder.get().getProperty()));
			}
			return recipeDtoBuilder.build();
		});
		return new PageImpl<>(recipes, pageable, findRecipesWithNutrimentsCountQuery(recipeDomainParametersDTO));
	}

	private void addFilterParameters(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO, MapSqlParameterSource mapSqlParameterSource) {
		recipeDomainParametersDTO.getSearchName().map(String::toUpperCase).ifPresent(searchName -> mapSqlParameterSource.addValue("recipeName", searchName));
		recipeDomainParametersDTO.getCategory().ifPresent(category -> mapSqlParameterSource.addValue("category", category));
		mapSqlParameterSource.addValue("username", recipeDomainParametersDTO.getUsername());
	}

	private long findRecipesWithNutrimentsCountQuery(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		addFilterParameters(recipeDomainParametersDTO, mapSqlParameterSource);
		Long count = jdbcTemplate.queryForObject(buildSelectRecipesCountQuery(recipeDomainParametersDTO), mapSqlParameterSource, Long.class);
		return count == null ? 0 : count;
	}

	private String buildSelectRecipesCountQuery(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO) {
		StringBuilder sql = new StringBuilder("select count(nlabs_recipes.*) resultNumber ");
		sql.append(buildFromRecipes());
		sql.append(buildLeftJoinUsers());
		recipeDomainParametersDTO.getCategory().ifPresent(category -> sql.append(buildJoinCategories()));
		sql.append(buildWhere(recipeDomainParametersDTO));
		return sql.toString();
	}

	private String buildSelectRecipesQuery(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO) {
		StringBuilder sql = new StringBuilder();
		sql.append(buildSelectRecipes(recipeDomainParametersDTO.getPageable()));
		sql.append(buildFromRecipes());
		sql.append(buildLeftJoinUsers());
		recipeDomainParametersDTO.getCategory().ifPresent(category -> sql.append(buildJoinCategories()));
		sql.append(buildWhere(recipeDomainParametersDTO));
		sql.append(buildSort(recipeDomainParametersDTO.getPageable()));
		sql.append(buildPagination(recipeDomainParametersDTO.getPageable()));
		return sql.toString();
	}

	private String buildSelectRecipes(Pageable pageable) {
		StringBuilder sql = new StringBuilder("select nlabs_recipes.*, ");
		sql.append(buildSubSelectNutriment("nutriment_1")).append(", ");
		sql.append(buildSubSelectNutriment("nutriment_2")).append(", ");
		sql.append(buildSubSelectNutriment("nutriment_3")).append(", ");
		sql.append(buildSubSelectNutriment("nutriment_4"));
		if (pageableContainsAdditionalPropertyToSort(pageable)) {
			sql.append(pageable.getSort().stream()
					.filter(order -> isAdditionalProperty(order))
					.map(order -> buildSubSelectNutriment(order.getProperty()))
					.collect(Collectors.joining(", ", ", ", " ")));
		}
		return sql.toString();
	}

	private String buildSubSelectNutriment(String nutrimentName) {
		return NutrimentSortSupport.buildSubSelectNutriment(nutrimentName, "nlabs_recipe_nutriments", "id_recipe", "nlabs_recipes");
	}

	private String buildFromRecipes() {
		return " from nlabs_recipes ";
	}

	private String buildLeftJoinUsers() {
		return " left outer join nlabs_recipe_users on nlabs_recipe_users.id_recipe = nlabs_recipes.id";
	}

	private String buildJoinCategories() {
		return " inner join nlabs_recipe_tags on nlabs_recipe_tags.id_recipe = nlabs_recipes.id ";
	}

	private String buildWhere(RecipesPaginatedByUserParametersDTO recipeDomainParametersDTO) {
		StringBuilder sqlFragment = new StringBuilder(" where ");
		recipeDomainParametersDTO.getSearchName().ifPresent(searchName -> sqlFragment.append(" upper(name) like '%' || :recipeName || '%' and "));
		sqlFragment.append(" (nlabs_recipe_users.username = :username ");
		sqlFragment.append(" or nlabs_recipe_users.username is null) ");
		recipeDomainParametersDTO.getCategory().ifPresent(category -> sqlFragment.append(" and nlabs_recipe_tags.id_tag = :category "));
		return sqlFragment.toString();
	}

	private String buildSort(Pageable pageable) {
		Sort sort = pageable.getSort();
		if (sort.stream().noneMatch(order -> "id".equals(order.getProperty()))) {
			sort = sort.and(Sort.by("id"));
		}
		return " order by " + buildOrderItems(sort);
	}

	private String buildPagination(Pageable pageable) {
		if (pageable.isPaged()) {
			return " offset :pOffset limit :pLimit ";
		}
		return "";
	}
}
