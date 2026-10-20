package com.nutritionlabs.dataaccess.commons;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * SQL fragments shared by the native queries that list foods and recipes with their nutriments.
 *
 * <p>A sort property is either a column of the main table or {@code nutriment_<id catalog>}, which is
 * resolved by a sub select over the nutriments table. Sort properties come from the request, so they are
 * validated before being written into the SQL.
 */
public final class NutrimentSortSupport {

	public static final String NUTRIMENT_PREFIX = "nutriment_";

	/**
	 * Nutriments 1 to 4 (energy, carbohydrate, fat and protein) are always selected.
	 */
	private static final int ADDITIONAL_PROPERTY_INDEX = 5;

	private static final Pattern COLUMN_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

	private static final Pattern NUTRIMENT_PATTERN = Pattern.compile(NUTRIMENT_PREFIX + "\\d{1,9}");

	private NutrimentSortSupport() {
	}

	/**
	 * Sub select that returns the quantity of a nutriment.
	 *
	 * @param nutrimentName    property {@code nutriment_<id catalog>}, also used as column alias
	 * @param nutrimentsTable  table of the nutriments
	 * @param ownerColumn      column of the nutriments table that references the main table
	 * @param mainTable        main table of the query
	 */
	public static String buildSubSelectNutriment(String nutrimentName, String nutrimentsTable, String ownerColumn, String mainTable) {
		validateNutriment(nutrimentName);
		String indexNutriment = nutrimentName.substring(NUTRIMENT_PREFIX.length());
		return "COALESCE((select quantity from " + nutrimentsTable
				+ " where " + nutrimentsTable + "." + ownerColumn + " = " + mainTable + ".id"
				+ " and " + nutrimentsTable + ".id_nutriment_catalog = " + indexNutriment + "), 0) as " + nutrimentName;
	}

	public static boolean isNutriment(Sort.Order order) {
		return order.getProperty().startsWith(NUTRIMENT_PREFIX);
	}

	/**
	 * Whether the order is a nutriment that is not selected by default.
	 */
	public static boolean isAdditionalProperty(Sort.Order order) {
		if (!isNutriment(order)) {
			return false;
		}
		validateNutriment(order.getProperty());
		return Integer.parseInt(order.getProperty().substring(NUTRIMENT_PREFIX.length())) >= ADDITIONAL_PROPERTY_INDEX;
	}

	public static boolean pageableContainsAdditionalPropertyToSort(Pageable pageable) {
		return pageable.getSort().stream().anyMatch(NutrimentSortSupport::isAdditionalProperty);
	}

	/**
	 * Order by clause items, without the {@code order by} keyword.
	 */
	public static String buildOrderItems(Sort sort) {
		return sort.stream()
				.map(order -> validateColumn(order.getProperty()) + " " + order.getDirection().name())
				.collect(Collectors.joining(", "));
	}

	private static String validateColumn(String property) {
		if (!COLUMN_PATTERN.matcher(property).matches()) {
			throw new IllegalArgumentException("Invalid sort property [" + property + "]");
		}
		return property;
	}

	private static void validateNutriment(String property) {
		if (!NUTRIMENT_PATTERN.matcher(property).matches()) {
			throw new IllegalArgumentException("Invalid nutriment property [" + property + "]");
		}
	}
}
