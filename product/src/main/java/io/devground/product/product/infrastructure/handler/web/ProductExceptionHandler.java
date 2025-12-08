package io.devground.product.product.infrastructure.handler.web;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.domain.vo.ProductDomainErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final HttpServletResponse response;

	@ExceptionHandler(ProductDomainException.class)
	public BaseResponse<String> handleDomainException(ProductDomainException ex) {

		ProductDomainErrorCode errorCode = ex.getErrorCode();
		int status = errorCode.getHttpStatus();

		response.setStatus(status);

		return BaseResponse.fail(status, errorCode.getMessage());
	}
}
