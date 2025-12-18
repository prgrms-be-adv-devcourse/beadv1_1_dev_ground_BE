package io.devground.dbay.cart.infrastructure.adapter.out.vo;

import lombok.NonNull;

public record CartProductsResponse(
		String productCode,
		String productSaleCode,
		String sellerCode,
		String title,
		String description,
		String thumbnail,
		String categoryName,
		long price
) {
}
