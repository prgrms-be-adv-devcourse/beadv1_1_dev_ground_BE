package io.devground.product.product.domain.vo.response;

import java.net.URL;
import java.util.List;

import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;

public record UpdateProductResponse(

	String productCode,
	String productSaleCode,
	String sellerCode,
	String title,
	String description,
	long price,
	List<URL> presignedUrl
) {
	public UpdateProductResponse(Product product, ProductSale productSale, List<URL> newPresignedUrls) {
		this(
			product.getCode(),
			productSale.getCode(),
			productSale.getSellerCode(),
			product.getProductSpec().title(),
			product.getProductSpec().description(),
			productSale.getProductSaleSpec().price(),
			newPresignedUrls
		);
	}
}