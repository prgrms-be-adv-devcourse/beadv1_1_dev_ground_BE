package io.devground.common.exceptionhandler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
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

	@ExceptionHandler(ServletRequestBindingException.class)
	public BaseResponse<String> handleServletRequestBindingException(ServletRequestBindingException ex) {

		int status = HttpStatus.FORBIDDEN.value();

		response.setStatus(status);

		return BaseResponse.fail(status, "필수 헤더가 누락되었습니다: " + ex.getMessage());
	}

	@ExceptionHandler(FeignException.class)
	public BaseResponse<String> handleFeignException(FeignException ex) {

		int status = ex.status();

		response.setStatus(status);

		return BaseResponse.fail(status, "외부 API 호출 실패: " + ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public BaseResponse<String> handleException(Exception ex) {

		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

		response.setStatus(status);

		return BaseResponse.fail(status, ex.getMessage());
	}
}
