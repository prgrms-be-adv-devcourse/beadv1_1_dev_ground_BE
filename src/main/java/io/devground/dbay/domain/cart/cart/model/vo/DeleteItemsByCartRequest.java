package io.devground.dbay.domain.cart.cart.model.vo;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record DeleteItemsByCartRequest(
	@NotEmpty(message = "삭제할 상품을 선택해주세요.")
	List<String> cartProductCodes
) {
}
