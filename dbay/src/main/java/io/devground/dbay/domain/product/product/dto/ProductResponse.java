package io.devground.dbay.domain.product.product.dto;

import java.util.List;

import io.devground.dbay.domain.product.product.vo.ProductStatus;
import lombok.Builder;

@Builder
public record ProductResponse(
	Long id,
	String code,
	String title,
	String description,
	Long categoryId,
	String categoryName,
	Long price,
	ProductStatus productStatus,
	List<String> imageUrls
) {
}
