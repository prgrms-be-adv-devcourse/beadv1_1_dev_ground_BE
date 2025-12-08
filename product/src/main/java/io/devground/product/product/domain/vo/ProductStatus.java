package io.devground.product.product.domain.vo;

public enum ProductStatus {
	ON_SALE("판매 중"),
	SOLD("판매 완료");

	private final String value;

	ProductStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
