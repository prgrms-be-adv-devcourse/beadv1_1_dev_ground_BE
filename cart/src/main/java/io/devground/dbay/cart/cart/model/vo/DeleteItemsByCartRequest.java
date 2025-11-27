package io.devground.dbay.cart.cart.model.vo;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteItemsByCartRequest(
	@NotEmpty(message = "삭제할 상품을 선택해주세요.")
	List<String> cartProductCodes
) {
}
