package io.devground.dbay.domain.product.product.mapper;

import io.devground.dbay.domain.product.product.dto.ProductDetailResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;

public abstract class ProductMapper {

	public static RegistProductResponse registResponseFromProductInfo(Product product, ProductSale productSale) {

		return RegistProductResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.price(productSale.getPrice())
			.build();
	}

	public static ProductDetailResponse detailFromProduct(Product product) {

		ProductSale productSale = product.getProductSale();

		return ProductDetailResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.categoryPath(product.getCategory().getFullPath())
			.price(productSale.getPrice())
			.productStatus(productSale.getProductStatus().getValue())
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
