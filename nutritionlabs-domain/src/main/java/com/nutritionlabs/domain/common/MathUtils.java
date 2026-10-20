package com.nutritionlabs.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

	public static final int FOOD_BASE_QUANTITY = 100;

	public static double calculateNutrimentQuantity(double nutrimentValue, double quantity, double totalQuantity, double totalGrams) {
		return truncateDecimal(((nutrimentValue * getTotalQuantity(quantity, totalGrams)) / totalQuantity), 2);
	}

	public static double getTotalQuantity(double quantity, double totalGrams) {
		return quantity * totalGrams;
	}

	public static double truncateDecimal(double x, int numberOfDecimals) {
		if (Double.isNaN(x) || Double.isInfinite(x)) {
			return 0;
		}
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberOfDecimals, RoundingMode.FLOOR).doubleValue();
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberOfDecimals, RoundingMode.CEILING).doubleValue();
		}
	}
}
