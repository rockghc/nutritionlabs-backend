package com.nutritionlabs.dataaccess.commons;

import org.hibernate.Hibernate;

public class CommonHibernateUtils {

	private CommonHibernateUtils() {
	}

	/**
	 * Returns the entity behind a Hibernate proxy, initializing it when needed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T initializeAndUnproxy(T entity) {
		if (entity == null) {
			throw new NullPointerException("Entity passed for initialization is null");
		}
		return (T) Hibernate.unproxy(entity);
	}
}
