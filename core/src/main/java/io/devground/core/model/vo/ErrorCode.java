package io.devground.core.model.vo;

import io.devground.core.model.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// common
	CODE_INVALID(400, "잘못된 코드 형식입니다."),

	// user/auth
	UNAUTHORIZED(401, "로그인이 필요합니다."),
	USER_NOT_FOUNT(404, "사용자를 찾을 수 없습니다."),
	CODE_EXPIRED(400, "인증 코드가 만료되었습니다."),
	WRONG_VERIFICATION_CODE(400, "인증번호가 올바르지 않습니다."),
	NOT_VERIFICATION_EMAIL(400, "이메일이 인증되지 않았습니다."),

	// cart
	CART_ALREADY_EXIST(409, "장바구니가 이미 존재합니다."),
	CART_NOT_FOUND(404, "장바구니를 찾을 수 없습니다."),

	// order
	ADDRESS_NOT_FOUND(404, "해당 주소를 찾을 수 없습니다."),
	ORDER_NOT_FOUND(404, "해당 주문을 찾을 수 없습니다."),

	// product
	ONLY_ON_SALE_PRODUCT_CHANGEABLE(400, "판매 중인 상품만 판매 완료 처리할 수 있습니다."),
	SOLD_PRODUCT_CANNOT_UPDATE(400, "이미 판매된 상품 내용은 변경할 수 없습니다."),
	PRODUCT_MUST_WITH_LEAF_CATEGORY(400, "상품은 반드시 최종 카테고리를 가져야 합니다."),
	PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다."),

	// category
	CANNOT_EXCEED_MAX_DEPTH(400, "카테고리는 최대 뎁스를 초과할 수 없습니다."),
	MISMATCH_ON_DEPTH(400, "해당 카테고리의 하위에 등록할 수 없습니다."),
	CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다."),

	// image
	IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다."),

	// deposit
	AMOUNT_MUST_BE_POSITIVE(400, "금액은 0보다 커야 합니다."),
	INSUFFICIENT_BALANCE(400, "잔액이 부족합니다.");

	private final int httpStatus;
	private final String message;

	public ServiceException throwServiceException() {
		throw new ServiceException(this);
	}

	public ServiceException throwServiceException(Throwable cause) {
		throw new ServiceException(this, cause);
	}
}
