package com.nutritionlabs.dataaccess.food;

import static com.nutritionlabs.dataaccess.commons.NutrimentSortSupport.buildOrderItems;
import static com.nutritionlabs.dataaccess.commons.NutrimentSortSupport.isAdditionalProperty;
import static com.nutritionlabs.dataaccess.commons.NutrimentSortSupport.pageableContainsAdditionalPropertyToSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.nutritionlabs.dataaccess.commons.NutrimentSortSupport;
import com.nutritionlabs.domain.food.dto.CatalogLabelDTO;
import com.nutritionlabs.domain.food.dto.FoodCommonMeasureDTO;
import com.nutritionlabs.domain.food.dto.FoodDTO;
import com.nutritionlabs.domain.food.dto.FoodDomainParametersDTO;

/**
 * Lists the foods of a type with their main nutriments (energy, carbohydrate, fat and protein).
 */
public class FoodCustomRepositoryImpl implements FoodCustomRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public FoodCustomRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Page<FoodDTO> findFoodsWithNutriments(FoodDomainParametersDTO foodDomainParametersDTO) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		Pageable pageable = foodDomainParametersDTO.getPageable();
		if (pageable.isPaged()) {
			mapSqlParameterSource.addValue("pOffset", pageable.getOffset());
			mapSqlParameterSource.addValue("pLimit", pageable.getPageSize());
		}
		addFilterParameters(foodDomainParametersDTO, mapSqlParameterSource);
		Optional<Sort.Order> additionalOrder = pageable.getSort().stream().findFirst().filter(order -> isAdditionalProperty(order));
		List<FoodDTO> foods = jdbcTemplate.query(buildSelectFoodsQuery(foodDomainParametersDTO), mapSqlParameterSource, (rs, rowNum) -> {
			FoodDTO.FoodDTOBuilder foodDtoBuilder = FoodDTO.builder()
					.id(rs.getLong("id"))
					.name(rs.getString("name"))
					.quantity(rs.getLong("quantity"))
					.foodGroup(rs.getLong("food_group"))
					.energy(rs.getDouble("nutriment_1"))
					.carbohydrate(rs.getDouble("nutriment_2"))
					.fat(rs.getDouble("nutriment_3"))
					.protein(rs.getDouble("nutriment_4"))
					.source(CatalogLabelDTO.builder().id(rs.getLong("id_source")).build());
			if (additionalOrder.isPresent()) {
				foodDtoBuilder.micronutrientQuantity(rs.getDouble(additionalOrder.get().getProperty()));
			}
			return foodDtoBuilder.build();
		});
		if (foodDomainParametersDTO.isWithCommonMeasures()) {
			Map<Long, List<FoodCommonMeasureDTO>> commonMeasures = getCommonMeasures(foods.stream().map(FoodDTO::getId).collect(Collectors.toList()));
			foods.forEach(food -> food.setCommonMeasures(commonMeasures.get(food.getId())));
		}
		return new PageImpl<>(foods, pageable, findFoodsWithNutrimentsCountQuery(foodDomainParametersDTO));
	}

	private Map<Long, List<FoodCommonMeasureDTO>> getCommonMeasures(List<Long> idsFoods) {
		List<FoodCommonMeasureDTO> commonMeasures = new ArrayList<>();
		if (!idsFoods.isEmpty()) {
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
			mapSqlParameterSource.addValue("idsFoods", idsFoods);
			String query = "SELECT id, id_food, singular_name, plural_name, quantity, total_grams, edible_portion "
					+ "FROM nlabs_food_common_measures "
					+ "where id_food in ( :idsFoods )";
			commonMeasures = jdbcTemplate.query(query, mapSqlParameterSource, (rs, rowNum) -> FoodCommonMeasureDTO.builder()
					.id(rs.getLong("id"))
					.idFood(rs.getLong("id_food"))
					.singularName(rs.getString("singular_name"))
					.pluralName(rs.getString("plural_name"))
					.quantity(rs.getDouble("quantity"))
					.totalGrams(rs.getDouble("total_grams"))
					.ediblePortion(rs.getDouble("edible_portion"))
					.build());
		}
		return commonMeasures.stream().collect(Collectors.groupingBy(FoodCommonMeasureDTO::getIdFood));
	}

	private void addFilterParameters(FoodDomainParametersDTO foodDomainParametersDTO, MapSqlParameterSource mapSqlParameterSource) {
		mapSqlParameterSource.addValue("foodType", foodDomainParametersDTO.getFoodType());
		foodDomainParametersDTO.getSearchName().map(String::toUpperCase).ifPresent(searchName -> mapSqlParameterSource.addValue("foodName", searchName));
		foodDomainParametersDTO.getSource().ifPresent(source -> mapSqlParameterSource.addValue("source", source));
		mapSqlParameterSource.addValue("username", foodDomainParametersDTO.getUsername());
	}

	private long findFoodsWithNutrimentsCountQuery(FoodDomainParametersDTO foodDomainParametersDTO) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		addFilterParameters(foodDomainParametersDTO, mapSqlParameterSource);
		Long count = jdbcTemplate.queryForObject(buildSelectFoodsCountQuery(foodDomainParametersDTO), mapSqlParameterSource, Long.class);
		return count == null ? 0 : count;
	}

	private String buildSelectFoodsCountQuery(FoodDomainParametersDTO foodDomainParametersDTO) {
		return "select count(nlabs_foods.*) resultNumber "
				+ buildFromFoods()
				+ buildLeftJoinUsers()
				+ buildWhere(foodDomainParametersDTO);
	}

	private String buildSelectFoodsQuery(FoodDomainParametersDTO foodDomainParametersDTO) {
		return buildSelectFoods(foodDomainParametersDTO.getPageable())
				+ buildFromFoods()
				+ buildLeftJoinUsers()
				+ buildWhere(foodDomainParametersDTO)
				+ buildSort(foodDomainParametersDTO.getPageable())
				+ buildPagination(foodDomainParametersDTO.getPageable());
	}

	private String buildSelectFoods(Pageable pageable) {
		StringBuilder sql = new StringBuilder("select nlabs_foods.*, ");
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
		return NutrimentSortSupport.buildSubSelectNutriment(nutrimentName, "nlabs_foods_nutriments", "id_food", "nlabs_foods");
	}

	private String buildFromFoods() {
		return " from nlabs_foods ";
	}

	private String buildLeftJoinUsers() {
		return " left outer join nlabs_foods_users on nlabs_foods_users.id_food = nlabs_foods.id";
	}

	private String buildWhere(FoodDomainParametersDTO foodDomainParametersDTO) {
		StringBuilder sqlFragment = new StringBuilder();
		sqlFragment.append(" where food_type = :foodType ");
		foodDomainParametersDTO.getSearchName().ifPresent(searchName -> sqlFragment.append(" and upper(name) like '%' || :foodName || '%' "));
		foodDomainParametersDTO.getSource().ifPresent(source -> sqlFragment.append(" and id_source = :source "));
		sqlFragment.append(" and (nlabs_foods_users.username = :username ");
		sqlFragment.append(" or nlabs_foods_users.username is null) ");
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
