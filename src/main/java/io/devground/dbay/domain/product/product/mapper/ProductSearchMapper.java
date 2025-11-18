package io.devground.dbay.domain.product.product.mapper;

import org.springframework.data.elasticsearch.core.SearchHit;

import io.devground.dbay.domain.product.product.model.dto.ProductSearchResponse;
import io.devground.dbay.domain.product.product.model.entity.ProductDocument;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductSearchMapper {

	public ProductSearchResponse toProductSearchResponse(SearchHit<ProductDocument> searchHit) {

		ProductDocument doc = searchHit.getContent();

		return ProductSearchResponse.builder()
			.id(doc.getId())
			.productCode(doc.getProductCode())
			.title(doc.getTitle())
			.description(doc.getDescription())
			.thumbnailUrl(doc.getThumbnailUrl())
			.categoryName(doc.getCategoryName())
			.categoryFullPath(doc.getCategoryFullPath())
			.price(doc.getPrice() != null ? doc.getPrice() : 0L)
			.productStatus(doc.getProductStatus())
			.createdAt(doc.getCreatedAt())
			.score(searchHit.getScore())
			.build();
	}
}
