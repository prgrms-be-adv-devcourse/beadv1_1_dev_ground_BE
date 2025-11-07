package io.devground.core.model.vo;

import io.devground.core.model.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// user/auth
	UNAUTHORIZED(401, "로그인이 필요합니다."),

	// cart
	CART_NOT_FOUND(404, "장바구니를 찾을 수 없습니다."),

	// product
	PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다."),

	// image
	IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다.");

	private final int httpStatus;
	private final String message;

	public ServiceException throwServiceException() {
		throw new ServiceException(this);
	}

	public ServiceException throwServiceException(Throwable cause) {
		throw new ServiceException(this, cause);
	}
}
