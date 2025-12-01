package io.devground.product.infrastructure.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
	ON_SALE("판매 중"),
	SOLD("판매 완료");

	private final String value;
}
