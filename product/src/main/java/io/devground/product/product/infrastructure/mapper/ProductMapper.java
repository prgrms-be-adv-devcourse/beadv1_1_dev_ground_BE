package io.devground.product.product.infrastructure.mapper;

import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;
import io.devground.product.product.domain.vo.ProductSaleSpec;
import io.devground.product.product.domain.vo.ProductSpec;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.model.persistence.ProductSaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

	public Product toProductDomain(ProductEntity productEntity, ProductSaleEntity productSaleEntity) {

		Product product = new Product(
			new ProductSpec(productEntity.getTitle(), productEntity.getDescription()),
			CategoryMapper.toDomain(productEntity.getCategory())
		);

		product.updateCode(productEntity.getCode());
		product.updateThumbnail(productEntity.getThumbnailUrl());
		product.linkProductSale(toProductSaleDomain(productEntity, productSaleEntity));
		product.updateId(productEntity.getId());

		return product;
	}

	public ProductSale toProductSaleDomain(ProductEntity productEntity, ProductSaleEntity productSaleEntity) {

		ProductSale productSale = new ProductSale(
			productSaleEntity.getSellerCode(),
			productEntity.getCode(),
			new ProductSaleSpec(productSaleEntity.getPrice(), productSaleEntity.getProductStatus())
		);

		productSale.updateCode(productSaleEntity.getCode());

		return productSale;
	}
}
