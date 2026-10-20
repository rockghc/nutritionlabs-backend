package com.nutritionlabs.errors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nutritionlabs.exceptions.EntityNotFoundException;
import com.nutritionlabs.exceptions.FileNameException;

import lombok.extern.slf4j.Slf4j;

/**
 * Converts the exceptions of the controllers into {@link ApiError} responses.
 *
 * Thanks to Bruno Cleite
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String error = ex.getParameterName() + " parameter is missing";
		return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
		return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage("Validation error");
		apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
		apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
		return buildResponseEntity(apiError);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		if (request instanceof ServletWebRequest servletWebRequest) {
			log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
		}
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error writing JSON output", ex));
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
		apiError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	/**
	 * Thrown when a {@code @Validated} validation fails.
	 */
	@ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
	protected ResponseEntity<Object> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage("Validation error");
		apiError.addValidationErrors(ex.getConstraintViolations());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
		ApiError apiError = new ApiError(NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
		return buildResponseEntity(new ApiError(NOT_FOUND, ex));
	}

	/**
	 * Inspects the cause for the different database errors.
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
			return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", ex.getCause()));
		}
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	protected ResponseEntity<Object> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
		return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "The entity was modified by another request", ex));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "?";
		apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), requiredType));
		apiError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class, FileNameException.class })
	protected ResponseEntity<Object> handleBadRequest(RuntimeException ex) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(ResponseStatusException.class)
	protected ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		ApiError apiError = new ApiError(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
		apiError.setMessage(ex.getReason());
		return buildResponseEntity(apiError);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
