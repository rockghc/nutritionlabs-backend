package com.nutritionlabs.errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Body of the error responses, serialized as {@code {"apierror": {...}}}.
 *
 * Thanks to Bruno Cleite
 */
@Getter
@Setter
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("apierror")
class ApiError {

	private HttpStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

	private String message;

	private String debugMessage;

	private List<ApiValidationError> subErrors;

	private ApiError() {
		timestamp = LocalDateTime.now();
	}

	ApiError(HttpStatus status) {
		this();
		this.status = status;
	}

	ApiError(HttpStatus status, Throwable ex) {
		this();
		this.status = status;
		this.message = "Unexpected error";
		this.debugMessage = ex.getLocalizedMessage();
	}

	ApiError(HttpStatus status, String message, Throwable ex) {
		this();
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	private void addSubError(ApiValidationError subError) {
		if (subErrors == null) {
			subErrors = new ArrayList<>();
		}
		subErrors.add(subError);
	}

	void addValidationErrors(List<FieldError> fieldErrors) {
		fieldErrors.forEach(fieldError -> addSubError(new ApiValidationError(fieldError.getObjectName(), fieldError.getField(),
				fieldError.getRejectedValue(), fieldError.getDefaultMessage())));
	}

	void addValidationError(List<ObjectError> globalErrors) {
		globalErrors.forEach(objectError -> addSubError(new ApiValidationError(objectError.getObjectName(), null, null,
				objectError.getDefaultMessage())));
	}

	void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
		constraintViolations.forEach(constraintViolation -> addSubError(new ApiValidationError(
				constraintViolation.getRootBeanClass().getSimpleName(),
				constraintViolation.getPropertyPath().toString(),
				constraintViolation.getInvalidValue(),
				constraintViolation.getMessage())));
	}

	@Getter
	@Setter
	@AllArgsConstructor
	static class ApiValidationError {
		private String object;
		private String field;
		private Object rejectedValue;
		private String message;
	}
}
