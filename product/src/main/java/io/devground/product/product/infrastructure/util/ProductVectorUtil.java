package io.devground.product.product.infrastructure.util;

import java.util.Map;

import org.springframework.ai.document.Document;

import io.devground.product.product.domain.model.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductVectorUtil {

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
