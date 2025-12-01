package io.devground.product.domain.vo;

import io.devground.product.domain.exception.DomainException;

public enum DomainErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// category
	CANNOT_EXCEED_MAX_DEPTH(400, "카테고리는 최대 뎁스를 초과할 수 없습니다."),
	MISMATCH_ON_DEPTH(400, "해당 카테고리의 하위에 등록할 수 없습니다."),
	CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다."),

	// product
	ONLY_ON_SALE_PRODUCT_CHANGEABLE(400, "판매 중인 상품만 판매 완료 처리할 수 있습니다."),
	SOLD_PRODUCT_CANNOT_PURCHASE(400, "이미 판매된 상품입니다."),
	SOLD_PRODUCT_CANNOT_UPDATE(400, "이미 판매된 상품 내용은 변경할 수 없습니다."),
	PRODUCT_MUST_WITH_LEAF_CATEGORY(400, "상품은 반드시 최종 카테고리를 가져야 합니다."),
	IS_NOT_PRODUCT_OWNER(403, "해당 상품의 판매자가 아닙니다"),
	PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다.");

	private final int httpStatus;
	private final String message;

	DomainErrorCode(int httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public DomainException throwException() {
		throw new DomainException(this);
	}

	public DomainException throwException(Throwable cause) {
		throw new DomainException(this, cause);
	}

	public static DomainErrorCode fromHttpStatus(int httpStatus, String msg) {
		for (DomainErrorCode errorCode : DomainErrorCode.values()) {
			if (errorCode.getHttpStatus() == httpStatus && errorCode.getMessage().equals(msg)) {
				return errorCode;
			}
		}

		return INTERNAL_SERVER_ERROR;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getMessage() {
		return message;
	}
	}