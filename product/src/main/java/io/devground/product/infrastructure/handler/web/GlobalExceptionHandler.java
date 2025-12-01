package io.devground.product.infrastructure.handler.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.devground.product.domain.exception.DomainException;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.infrastructure.exception.InfraException;
import io.devground.product.infrastructure.model.web.BaseResponse;
import io.devground.product.infrastructure.vo.InfraErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final HttpServletResponse response;

	@ExceptionHandler(DomainException.class)
	public BaseResponse<String> handleDomainException(DomainException ex) {

		DomainErrorCode errorCode = ex.getErrorCode();
		int status = errorCode.getHttpStatus();

		response.setStatus(status);

		return BaseResponse.fail(status, errorCode.getMessage());
	}

	@ExceptionHandler(InfraException.class)
	public BaseResponse<String> handleInfraException(InfraException ex) {

		InfraErrorCode errorCode = ex.getErrorCode();
		int status = errorCode.getHttpStatus();

		response.setStatus(status);

		return BaseResponse.fail(status, errorCode.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public BaseResponse<String> handleValidationException(MethodArgumentNotValidException ex) {

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

		StringBuilder errorMessages = new StringBuilder();
		for (FieldError fieldError : fieldErrors) {
			errorMessages.append(fieldError.getField())
				.append(": ")
				.append(fieldError.getDefaultMessage())
				.append("\n");
		}

		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

		response.setStatus(status);

		return BaseResponse.fail(status, errorMessages.toString());
	}

	@ExceptionHandler(Exception.class)
	public BaseResponse<String> handleException(Exception ex) {

		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

		response.setStatus(status);

		return BaseResponse.fail(status, ex.getMessage());
	}
}
