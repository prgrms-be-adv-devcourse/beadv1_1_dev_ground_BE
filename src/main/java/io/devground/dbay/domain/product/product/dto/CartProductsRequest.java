package io.devground.dbay.domain.product.product.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record CartProductsRequest(

	@NotNull(message = "상품 코드 목록은 반드시 전달되어야 합니다.")
	List<String> productCodes
) {
}
