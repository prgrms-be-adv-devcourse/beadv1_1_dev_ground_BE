package io.devground.product.application.model;

import io.devground.product.domain.vo.ProductSaleSpec;

public record UpdateProductSoldDto(

	String productCode,
	ProductSaleSpec productSaleSpec
) {
}
