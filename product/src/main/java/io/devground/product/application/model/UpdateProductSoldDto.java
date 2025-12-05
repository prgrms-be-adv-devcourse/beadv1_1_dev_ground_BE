package io.devground.product.application.model;

import io.devground.product.domain.vo.ProductStatus;

public record UpdateProductSoldDto(

	String productCode,
	ProductStatus productStatus
) {
}
