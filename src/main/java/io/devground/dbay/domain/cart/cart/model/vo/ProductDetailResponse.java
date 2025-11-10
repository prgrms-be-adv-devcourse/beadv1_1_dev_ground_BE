package io.devground.dbay.domain.cart.cart.model.vo;

public record ProductDetailResponse(
	String code,
	String productName,
	long price,
	String productStatus
) {
}
