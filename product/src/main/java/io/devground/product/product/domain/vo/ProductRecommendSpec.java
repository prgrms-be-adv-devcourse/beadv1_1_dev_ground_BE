package io.devground.product.product.domain.vo;

public record ProductRecommendSpec(

	String productCode,
	String title,
	String description,
	Long price,
	String categoryFullPath
) {
}
