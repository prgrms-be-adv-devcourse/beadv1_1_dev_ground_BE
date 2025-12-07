package io.devground.dbay.order.infrastructure.vo;

import io.devground.dbay.order.domain.vo.ProductStatus;

import java.util.List;

public record ProductDetailResponse(
	String productCode,

	String productSaleCode,

	String sellerCode,

	String title,

	String description,

	String categoryPath,

	long price,

	ProductStatus productStatus,

	List<String> imageUrls
) {
}
