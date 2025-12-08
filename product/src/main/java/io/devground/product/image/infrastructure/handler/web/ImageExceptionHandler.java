package io.devground.product.image.infrastructure.handler.web;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.devground.core.model.web.BaseResponse;
import io.devground.product.image.domain.exception.ImageDomainException;
import io.devground.product.image.domain.vo.ImageDomainErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Order(1)
@RestControllerAdvice
@RequiredArgsConstructor
public class ImageExceptionHandler {

	private final HttpServletResponse response;

	@ExceptionHandler(ImageDomainException.class)
	public BaseResponse<String> handleDomainException(ImageDomainException ex) {

		ImageDomainErrorCode errorCode = ex.getErrorCode();
		int status = errorCode.getHttpStatus();

		response.setStatus(status);

		return BaseResponse.fail(status, errorCode.getMessage());
	}
}
