package io.devground.product.domain.vo.response;

import java.net.URL;
import java.util.List;

import io.devground.product.domain.model.Product;
import io.devground.product.domain.model.ProductSale;

public record RegistProductResponse(

	String productCode,
	String productSaleCode,
	String sellerCode,
	String title,
	String description,
	long price,
	List<URL> presignedUrls
) {
	public RegistProductResponse(Product product, ProductSale productSale, List<URL> presignedUrls) {
		this(
			product.getCode(),
			productSale.getCode(),
			productSale.getSellerCode(),
			product.getProductSpec().title(),
			product.getProductSpec().description(),
			productSale.getProductSaleSpec().price(),
			presignedUrls
		);
	}
}