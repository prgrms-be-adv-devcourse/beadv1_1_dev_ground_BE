package io.devground.dbay.cart.cart.model.vo;

import java.util.List;

public record ProductDetailResponse(
	String productCode,

	String productSaleCode,

	String sellerCode,

	String title,

	String description,

	String categoryPath,

	long price,

	String productStatus,

	List<String> imageUrls
) {
}
