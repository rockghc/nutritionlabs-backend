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

	public static final String KEYCLOAK_USER = USER + "/keycloakuser";
	public static final String USER_PROFILE = USER + "/profile";

	/** Genders catalog (id 11 of nlabs_catalogs), public because the sign up form needs it before login. */
	public static final String CATALOG_GENDERS = CATALOG + "/catalog/11";

	/** Countries catalog (id 12 of nlabs_catalogs), public because the sign up form needs it before login. */
	public static final String CATALOG_COUNTRIES = CATALOG + "/catalog/12";

	/** Phone prefixes catalog (id 13 of nlabs_catalogs), public because the sign up form needs it before login. */
	public static final String CATALOG_DIALING_CODES = CATALOG + "/catalog/13";

	/** Professions catalog (id 20 of nlabs_catalogs), public because the sign up form needs it before login. */
	public static final String CATALOG_PROFESSIONS = CATALOG + "/catalog/20";

	private ResourcePaths() {
	}
}
