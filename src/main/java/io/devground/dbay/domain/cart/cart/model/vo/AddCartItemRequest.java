package io.devground.dbay.domain.cart.cart.model.vo;

import jakarta.validation.constraints.NotBlank;

public record AddCartItemRequest(
	@NotBlank(message = "상품 코드가 누락되었습니다.")
	String productCode
) {
}
