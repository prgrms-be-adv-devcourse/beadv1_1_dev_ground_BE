package io.devground.product.domain.vo.response;

import java.util.List;

import io.devground.product.domain.model.Product;

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
	public ProductDetailResponse(Product product, List<String> imageUrls) {
		this(
			product.getCode(),
			product.getProductSale().getCode(),
			product.getProductSale().getSellerCode(),
			product.getProductSpec().title(),
			product.getProductSpec().description(),
			product.getCategory().getFullPath(),
			product.getProductSale().getProductSaleSpec().price(),
			product.getProductSale().getProductSaleSpec().productStatus().getValue(),
			imageUrls
		);
	}
}
