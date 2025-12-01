package io.devground.product.infrastructure.saga.vo;

import lombok.Getter;

@Getter
public enum SagaType {

	PRODUCT_IMAGE_REGIST,
	PRODUCT_IMAGE_UPDATE,
	PRODUCT_IMAGE_DELETE,

	// Settlement
	SETTLEMENT_DEPOSIT_CHARGE
}
