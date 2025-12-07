package io.devground.product.domain.vo.request;

import io.devground.product.domain.vo.ProductStatus;

public record UpdateProductSoldDto(

	String productCode,
	ProductStatus productStatus
) {
}
