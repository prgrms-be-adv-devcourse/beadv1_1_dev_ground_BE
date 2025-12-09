package io.devground.dbay.domain.product.product.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSearchRequest(

	@Schema(description = "검색 키워드")
	String keyword,

	@Schema(description = "검색 카테고리 IDs")
	List<Long> categoryIds,

	@Schema(description = "검색 최소가")
	Long minPrice,

	@Schema(description = "검색 최대가")
	Long maxPrice,

	@Schema(description = "검색 판매자 코드")
	String sellerCode,

	@Schema(description = "검색 상품 판매 상태")
	String productStatus,

	@Schema(description = "검색 상품 정렬 필드")
	String sortBy,

	@Schema(description = "검색 상품 정렬 방향")
	String sortDirection,

	@Schema(description = "검색 상품 페이지", example = "1")
	Integer page,

	@Schema(description = "검색 상품 페이지 내 컨텐츠 총 개수", example = "5")
	Integer size
) {
	public ProductSearchRequest {
		if (sortBy == null) {
			sortBy = "createdAt";
		}

		if (sortDirection == null) {
			sortDirection = "desc";
		}

		if (page == null || page < 1) {
			page = 1;
		}

		if (size == null || size <= 0 || size > 100) {
			size = 10;
		}
	}
}
