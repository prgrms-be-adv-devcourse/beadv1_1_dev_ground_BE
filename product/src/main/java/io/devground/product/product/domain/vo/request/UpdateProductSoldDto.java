package io.devground.product.product.domain.vo.request;

import io.devground.product.product.domain.vo.ProductStatus;

public record UpdateProductSoldDto(

	String productCode,
	ProductStatus productStatus
) {
}
