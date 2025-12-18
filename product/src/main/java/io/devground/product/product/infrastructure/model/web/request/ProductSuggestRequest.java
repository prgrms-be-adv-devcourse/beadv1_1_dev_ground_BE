package io.devground.product.product.infrastructure.model.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSuggestRequest(

	@Schema(description = "검색 키워드")
	String keyword,

	@Schema(description = "카테고리 ID")
	Long categoryId,

	@Schema(description = "판매 완료 상품 포함 여부", example = "false")
	Boolean includeSold,

	@Schema(description = "추천 개수", example = "10")
	Integer size
) {
	public ProductSuggestRequest {

		if (includeSold == null) {
			includeSold = false;
		}

		if (size == null || size <= 0 || size > 100) {
			size = 10;
		}
	}
}