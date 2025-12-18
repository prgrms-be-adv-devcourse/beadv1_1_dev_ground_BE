package io.devground.product.product.domain.vo.response;

import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.model.persistence.ProductSaleEntity;

public record GetAllProductsResponse(

	String productCode,
	String title,
	String thumbnailUrl,
	String productStatus,
	long price
) {
	public GetAllProductsResponse(ProductEntity product, ProductSaleEntity productSale) {
		this(
			product.getCode(),
			product.getTitle(),
			product.getThumbnailUrl(),
			productSale.getProductStatus().getValue(),
			productSale.getPrice()
		);
	}
}
