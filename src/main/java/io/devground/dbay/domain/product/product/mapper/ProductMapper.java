package io.devground.dbay.domain.product.product.mapper;

import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;

public abstract class ProductMapper {

	public static RegistProductResponse registResponseFromProductInfos(Product product, ProductSale productSale) {

		return RegistProductResponse.builder()
			.productId(product.getId())
			.productSaleId(productSale.getId())
			.sellerCode(productSale.getSellerCode())
			.price(productSale.getPrice())
			.build();
	}

	public static UpdateProductResponse updateResponseFromProductInfo(Product product, ProductSale productSale) {

		return UpdateProductResponse.builder()
			.productId(product.getId())
			.productSaleId(productSale.getId())
			.sellerCode(productSale.getSellerCode())
			.price(productSale.getPrice())
			.build();
	}
}
