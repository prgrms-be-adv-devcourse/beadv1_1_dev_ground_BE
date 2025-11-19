package io.devground.dbay.domain.product.product.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSuggestRequest(

	@Schema(description = "검색 키워드", example = "갤럭")
	String keyword,

	@Schema(description = "카테고리 ID(옵션, COMPLETION에서 카테고리별 자동완성 제공)", example = "1")
	Long categoryId,

	@Schema(description = "판매 완료 상품 포함 여부", defaultValue = "false")
	Boolean includeSold,

	@Schema(description = "제안 개수", example = "10", defaultValue = "10")
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
