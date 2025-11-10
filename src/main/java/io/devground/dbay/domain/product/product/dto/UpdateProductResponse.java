package io.devground.dbay.domain.product.product.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record UpdateProductResponse(

	long productId,

	long productSaleId,

	@NonNull
	String sellerCode,

	long price
) {
}
