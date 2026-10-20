package com.nutritionlabs.service;

/**
 * Root paths of the REST resources. They are the routes of the former Zuul gateway, so the front end keeps its
 * URLs.
 */
public final class ResourcePaths {

	public static final String USER = "/resourceUser";
	public static final String CATALOG = "/resourceCatalog";
	public static final String FOOD = "/resourceFood";
	public static final String RECIPE = "/resourceRecipe";
	public static final String FILE = "/resourceFile";
	public static final String REPORT = "/resourceReport";

	private ResourcePaths() {
	}
}
