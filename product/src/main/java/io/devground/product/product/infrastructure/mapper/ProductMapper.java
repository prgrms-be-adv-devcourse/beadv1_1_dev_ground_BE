package io.devground.product.product.infrastructure.mapper;

import java.util.Map;

import org.springframework.ai.document.Document;

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

	public Document toVectorDocument(Product product) {

		String content = """
			카테고리: %s
			상품명: %s
			설명: %s
			""".formatted(
			product.getCategory().getFullPath(),
			product.getProductSpec().title(),
			product.getProductSpec().description()
		);

		Map<String, Object> metadata = Map.of(
			"productCode", product.getCode(),
			"categoryId", product.getCategory().getId(),
			"categoryFullPath", product.getCategory().getFullPath(),
			"title", product.getProductSpec().title(),
			"thumbnailUrl", product.getThumbnailUrl(),
			"price", product.getProductSale().getProductSaleSpec().price(),
			"productStatus", product.getProductSale().getProductSaleSpec().productStatus(),
			"deleteStatus", product.getDeleteStatus()
		);

		return new Document(product.getCode(), content, metadata);
	}
}
