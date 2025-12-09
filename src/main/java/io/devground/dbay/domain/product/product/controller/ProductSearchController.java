package io.devground.dbay.domain.product.product.controller;

import static org.springframework.http.HttpStatus.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.core.model.web.PageDto;
import io.devground.dbay.domain.product.product.model.dto.ProductSearchRequest;
import io.devground.dbay.domain.product.product.model.dto.ProductSearchResponse;
import io.devground.dbay.domain.product.product.model.dto.ProductSuggestRequest;
import io.devground.dbay.domain.product.product.model.dto.ProductSuggestResponse;
import io.devground.dbay.domain.product.product.service.ProductSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "ProductSearchController", description = "상품 검색 API. 엘라스틱 서치 사용")
public class ProductSearchController {

	private final ProductSearchService productSearchService;

	@GetMapping("/search")
	@Operation(summary = "상품 검색 By Elasticsearch",
		description = """
			Elasticsearch를 활용한 상품 검색 API입니다.
			NULL 값을 입력하면 해당 필터는 전체 검색으로 동작합니다.
			
			지원하는 기능은 아래와 같습니다.
			
			- Nori tokenizer를 사용한 한국어 형태소 분석
			- 동의어 처리
			- 최대 2글자까지의 오타 허용
			- N-gram을 통한 부분 검색
			- 자동 완성
			- 멀티 카테고리 필터
			- 가격 범위 필터
			- 판매자 필터
			- 상품 상태 필터
			- 정렬(최신순, 가격순, 관련도순)
			
			검색 예시는 아래와 같습니다.
			- keyword=샘숭 -> 동의어 처리되어 삼성으로 검색
			- categoryIds=1,2,3, -> 멀티 카테고리(생략 시 전체 카테고리 검색)
			- minPrice=10000&maxPrice=50000 -> 가격 범위
			- sortBy=price&sortDirection=ASC -> 가격 오름차순
			"""
	)
	public BaseResponse<PageDto<ProductSearchResponse>> searchProducts(@ParameterObject ProductSearchRequest request) {

		return BaseResponse.success(
			OK.value(),
			productSearchService.searchProducts(request),
			"상품 검색이 성공적으로 완료되었습니다."
		);
	}

	@GetMapping("/suggest/completion")
	@Operation(summary = "검색어 자동완성 By Elasticsearch",
		description = """
			Elasticsearch Suggest를 이용한 검색어 자동완성 API입니다.
			NULL 값을 입력하면 해당 필터는 전체 검색으로 동작합니다.
			
			- Title 및 CategoryName 기준 prefix에 따른 자동완성
			- 삭제된 상품은 제외
			- 카테고리별 자동완성(CategoryId) 지원
			"""
	)
	public BaseResponse<ProductSuggestResponse> suggestCompletion(@ParameterObject ProductSuggestRequest request) {

		return BaseResponse.success(
			OK.value(),
			productSearchService.suggestCompletion(request),
			"키워드 자동완성이 성공적으로 완료되었습니다."
		);
	}

	@GetMapping("/suggest/related")
	@Operation(summary = "연관 검색어 추천 By Elasticsearch",
		description = """
			Elasticsearch Suggest를 이용한 연관 검색어 추천 API입니다.
			NULL 값을 입력하면 해당 필터는 전체 검색으로 동작합니다.
			
			- Title, CategoryName, Description에서 Keyword와 함께 자주 등장하는 단어 집계
			- 삭제된 상품 및 기본값 기준 판매 완료 상품 제외
			"""
	)
	public BaseResponse<ProductSuggestResponse> suggestRelated(@ParameterObject ProductSuggestRequest request) {

		return BaseResponse.success(
			OK.value(),
			productSearchService.suggestRelated(request),
			"연관 검색어 추천이 성공적으로 완료되었습니다."
		);
	}
}
