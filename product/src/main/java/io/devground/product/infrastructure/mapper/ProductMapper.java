package io.devground.product.infrastructure.mapper;

import io.devground.product.domain.model.Product;
import io.devground.product.domain.model.ProductSale;
import io.devground.product.domain.vo.ProductSaleSpec;
import io.devground.product.domain.vo.ProductSpec;
import io.devground.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.infrastructure.model.persistence.ProductSaleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

	public Product toProductDomain(ProductEntity productEntity, ProductSaleEntity productSaleEntity) {

		Product product = new Product(
			new ProductSpec(productEntity.getTitle(), productEntity.getDescription(), productEntity.getThumbnailUrl()),
			CategoryMapper.toDomain(productEntity.getCategory())
		);

		product.linkProductSale(toProductSaleDomain(productSaleEntity));

		return product;
	}

	public ProductSale toProductSaleDomain(ProductSaleEntity productSaleEntity) {

		return new ProductSale(
			productSaleEntity.getSellerCode(),
			new ProductSaleSpec(productSaleEntity.getPrice(), productSaleEntity.getProductStatus())
		);
	}
}
