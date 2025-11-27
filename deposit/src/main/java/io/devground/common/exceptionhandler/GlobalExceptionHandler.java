package io.devground.common.exceptionhandler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final HttpServletResponse response;

	@ExceptionHandler(ServiceException.class)
	public BaseResponse<String> handleServiceException(ServiceException ex) {

		ErrorCode errorCode = ex.getErrorCode();
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
