package io.devground.product.domain.vo.response;

import lombok.NonNull;

public record CartProductsResponse(

	@NonNull
	String productCode,

	@NonNull
	String productSaleCode,

	@NonNull
	String sellerCode,

	@NonNull
	String title,

	long price
) {
}
