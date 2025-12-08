package io.devground.product.product.infrastructure.mapper;

import org.springframework.data.elasticsearch.core.SearchHit;

import io.devground.product.product.domain.vo.response.ProductSearchResponse;
import io.devground.product.product.infrastructure.model.persistence.ProductDocument;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductSearchMapper {

	public ProductSearchResponse toSearchResponse(SearchHit<ProductDocument> hit) {

		ProductDocument document = hit.getContent();

		return new ProductSearchResponse(
			document.getId(),
			document.getProductCode(),
			document.getTitle(),
			document.getDescription(),
			document.getThumbnailUrl(),
			document.getCategoryName(),
			document.getCategoryFullPath(),
			document.getPrice(),
			document.getProductStatus(),
			document.getCreatedAt(),
			hit.getScore()
		);
	}
}
