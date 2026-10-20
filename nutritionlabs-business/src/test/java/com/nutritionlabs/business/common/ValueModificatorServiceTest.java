package com.nutritionlabs.business.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.nutritionlabs.domain.common.dto.SingleValueDTO;

class ValueModificatorServiceTest {

	enum Status { ACTIVE, INACTIVE }

	static class Sample {
		LocalDate date;
		LocalDateTime dateTime;
		LocalTime time;
		Integer boxedInt;
		int primitiveInt;
		double primitiveDouble;
		Float boxedFloat;
		BigDecimal amount;
		Status status;
		String text;
	}

	private final ValueModificatorService service = new ValueModificatorService();

	private void modify(Sample sample, String property, Object value) {
		service.modifyValue(sample, SingleValueDTO.builder().propertyName(property).value(value).build());
	}

	@Test
	void convertsSupportedTypes() {
		Sample sample = new Sample();
		modify(sample, "date", "1990-05-17");
		modify(sample, "dateTime", "17/05/1990 08:30:00");
		modify(sample, "boxedInt", 12);
		modify(sample, "primitiveInt", "7");
		modify(sample, "primitiveDouble", 3.5);
		modify(sample, "boxedFloat", "1.25");
		modify(sample, "amount", "10.50");
		modify(sample, "status", "INACTIVE");
		modify(sample, "text", "notes");
		assertEquals(LocalDate.of(1990, 5, 17), sample.date);
		assertEquals(LocalDateTime.of(1990, 5, 17, 8, 30), sample.dateTime);
		assertEquals(12, sample.boxedInt);
		assertEquals(7, sample.primitiveInt);
		assertEquals(3.5, sample.primitiveDouble);
		assertEquals(1.25f, sample.boxedFloat);
		assertEquals(new BigDecimal("10.50"), sample.amount);
		assertEquals(Status.INACTIVE, sample.status);
		assertEquals("notes", sample.text);
	}

	@Test
	void timeOnlyUsesPlaceholderDate() {
		Sample sample = new Sample();
		modify(sample, "dateTime", "08:30:00");
		assertEquals(LocalDateTime.of(1987, 8, 4, 8, 30), sample.dateTime);
	}

	@Test
	void convertsTimes() {
		Sample sample = new Sample();
		modify(sample, "time", "08:30");
		assertEquals(LocalTime.of(8, 30), sample.time);
		modify(sample, "time", "21:05:10");
		assertEquals(LocalTime.of(21, 5, 10), sample.time);
		modify(sample, "time", "04/08/1987 06:45:00");
		assertEquals(LocalTime.of(6, 45), sample.time);
		modify(sample, "time", "2024-01-31T07:00");
		assertEquals(LocalTime.of(7, 0), sample.time);
	}

	@Test
	void nullClearsObjectFieldButRejectsPrimitive() {
		Sample sample = new Sample();
		sample.text = "x";
		modify(sample, "text", null);
		assertNull(sample.text);
		assertThrows(IllegalArgumentException.class, () -> modify(sample, "primitiveInt", null));
	}

	@Test
	void rejectsUnknownAndDisallowedProperties() {
		Sample sample = new Sample();
		assertThrows(IllegalStateException.class, () -> modify(sample, "missing", "x"));
		SingleValueDTO<String> dto = SingleValueDTO.<String>builder().propertyName("status").value("ACTIVE").build();
		assertThrows(IllegalArgumentException.class, () -> service.modifyValue(sample, dto, Set.of("text")));
		service.modifyValue(sample, dto, Set.of("status"));
		assertEquals(Status.ACTIVE, sample.status);
	}
}
