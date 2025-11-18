package io.devground.dbay.domain.product.product.model.dto;

import io.devground.dbay.domain.product.product.model.vo.SuggestType;

public record ProductSuggestRequest(

	// 추천 타입 -> COMPLETION: 자동 완성, PHRASE: 오타 수정, RELATED: 연관 검색어
	SuggestType type,

	String keyword,

	Long categoryId,

	// 추천 결과 개수
	int size
) {
	public ProductSuggestRequest {
		if (type == null) {
			type = SuggestType.COMPLETION;
		}

		if (size <= 0 || size > 100) {
			size = 10;
		}
	}
}
