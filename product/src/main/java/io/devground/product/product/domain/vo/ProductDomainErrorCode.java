package io.devground.product.product.domain.vo;

import io.devground.product.product.domain.exception.ProductDomainException;

public enum ProductDomainErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// common
	PAGE_MUST_BE_POSITIVE(400, "페이지 번호는 0보다 커야합니다."),
	PAGE_SIZE_MUST_BE_POSITIVE(400, "페이지 사이즈는 0보다 커야합니다."),

	// category domain validation
	CATEGORY_MUST_BE_INPUT(400, "카테고리는 반드시 입력되어야 합니다"),

	// product domain validation
	DESCRIPTION_MUST_BE_INPUT(400, "상품 설명은 반드시 입력되어야 합니다"),
	TITLE_MUST_BE_INPUT(400, "상품 이름은 반드시 입력되어야 합니다"),
	PRICE_MUST_BE_INPUT(400, "상품 가격은 반드시 입력되어야 합니다."),
	PRICE_MUST_BE_POSITIVE(400, "상품 가격은 반드시 양수여야 합니다."),
	SELLER_CODE_MUST_BE_INPUT(400, "판매자 코드는 반드시 입력되어야 합니다."),
	PRODUCT_CODE_MUST_BE_INPUT(400, "상품 코드는 반드시 입력되어야 합니다."),
	PRODUCT_STATUS_MUST_BE_INPUT(400, "상품 판매 상태는 반드시 입력되어야 합니다."),

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

	ProductDomainErrorCode(int httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public ProductDomainException throwException() {
		throw new ProductDomainException(this);
	}

	public ProductDomainException throwException(Throwable cause) {
		throw new ProductDomainException(this, cause);
	}

	public static ProductDomainErrorCode fromHttpStatus(int httpStatus, String msg) {
		for (ProductDomainErrorCode errorCode : ProductDomainErrorCode.values()) {
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