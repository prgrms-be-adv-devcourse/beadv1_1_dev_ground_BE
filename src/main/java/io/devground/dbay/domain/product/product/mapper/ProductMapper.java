package io.devground.dbay.domain.product.product.mapper;

import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;

public abstract class ProductMapper {

	public static RegistProductResponse registResponseFromProductInfos(Product product, ProductSale productSale) {

		return RegistProductResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.price(productSale.getPrice())
			.build();
	}

	public static UpdateProductResponse updateResponseFromProductInfo(Product product, ProductSale productSale) {

		return UpdateProductResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.price(productSale.getPrice())
			.build();
	}
}
