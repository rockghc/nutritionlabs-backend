package com.nutritionlabs.business.common;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.nutritionlabs.dataaccess.commons.CommonHibernateUtils;
import com.nutritionlabs.domain.common.dto.SingleValueDTO;

/**
 * Sets the value of a single property of an entity, converting the value sent by the client to the type
 * of the property.
 */
@Service
public class ValueModificatorService {

	private static final String DEFAULT_MISSING_DATE = "04/08/1987 ";
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
	private static final DateTimeFormatter DATE_TIME_FORMATTER_HOUR = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	private static final int MIN_DATE_HOUR_FORMATTABLE = 9;

	public <T> void modifyValue(T object, SingleValueDTO<?> singleValueDTO) {
		T innerEntity = CommonHibernateUtils.initializeAndUnproxy(object);
		setField(innerEntity, singleValueDTO.getPropertyName(), singleValueDTO.getValue());
	}

	public <T> void modifyValue(T object, SingleValueDTO<?> singleValueDTO, Set<String> allowedProperties) {
		if (!allowedProperties.contains(singleValueDTO.getPropertyName())) {
			throw new IllegalArgumentException("Property [" + singleValueDTO.getPropertyName() + "] is not modifiable");
		}
		modifyValue(object, singleValueDTO);
	}

	private void setField(Object target, String name, Object value) {
		Field field = ReflectionUtils.findField(target.getClass(), name);
		if (field == null) {
			throw new IllegalStateException("Could not find field [" + name + "] on target [" + target + "]");
		}
		ReflectionUtils.makeAccessible(field);
		convertAndSetValue(field, target, value);
	}

	private void convertAndSetValue(Field field, Object target, Object value) {
		if (value == null) {
			if (field.getType().isPrimitive()) {
				throw new IllegalArgumentException("Cannot set null on primitive field [" + field.getName() + "]");
			}
			ReflectionUtils.setField(field, target, null);
			return;
		}
		ReflectionUtils.setField(field, target, convertValue(field.getType(), value.toString()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object convertValue(Class<?> fieldType, String valueString) {
		Class<?> type = ClassUtils.resolvePrimitiveIfNecessary(fieldType);
		if (type.equals(LocalDate.class)) {
			return LocalDate.parse(valueString, DATE_FORMATTER);
		} else if (type.equals(LocalDateTime.class)) {
			if (valueString.length() < MIN_DATE_HOUR_FORMATTABLE) {
				valueString = DEFAULT_MISSING_DATE + valueString;
			}
			return LocalDateTime.parse(valueString, DATE_TIME_FORMATTER_HOUR);
		} else if (type.equals(LocalTime.class)) {
			return parseLocalTime(valueString);
		} else if (type.equals(Integer.class)) {
			return Integer.parseInt(valueString);
		} else if (type.equals(Long.class)) {
			return Long.parseLong(valueString);
		} else if (type.equals(Short.class)) {
			return Short.parseShort(valueString);
		} else if (type.equals(Boolean.class)) {
			return Boolean.parseBoolean(valueString);
		} else if (type.equals(Double.class)) {
			return Double.parseDouble(valueString);
		} else if (type.equals(Float.class)) {
			return Float.parseFloat(valueString);
		} else if (type.equals(BigDecimal.class)) {
			return new BigDecimal(valueString);
		} else if (type.equals(BigInteger.class)) {
			return new BigInteger(valueString);
		} else if (type.isEnum()) {
			return Enum.valueOf((Class<? extends Enum>) type, valueString);
		} else if (type.isAssignableFrom(String.class)) {
			return valueString;
		}
		throw new IllegalArgumentException("Unsupported field type [" + fieldType.getName() + "]");
	}

	/**
	 * Accepts a time ({@code HH:mm} or {@code HH:mm:ss}) or a date time ({@code dd/MM/yyyy HH:mm:ss}), in
	 * which case only the time is kept.
	 */
	private LocalTime parseLocalTime(String valueString) {
		String trimmed = valueString.trim();
		if (trimmed.length() >= MIN_DATE_HOUR_FORMATTABLE && trimmed.indexOf('/') > 0) {
			return LocalDateTime.parse(trimmed, DATE_TIME_FORMATTER_HOUR).toLocalTime();
		}
		int timeStart = Math.max(trimmed.lastIndexOf(' '), trimmed.lastIndexOf('T')) + 1;
		return LocalTime.parse(trimmed.substring(timeStart));
	}
}
