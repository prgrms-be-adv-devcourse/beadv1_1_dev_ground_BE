package io.devground.product.product.domain.vo.response;

import java.util.List;

import io.devground.product.product.domain.vo.ProductRecommendSpec;
import io.devground.product.product.domain.vo.RecommendType;

public record ProductRecommendResponse(

	RecommendType recommendType,
	List<ProductRecommendSpec> recommendSpecs
) {
}
