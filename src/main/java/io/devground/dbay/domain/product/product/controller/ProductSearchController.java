package io.devground.dbay.domain.product.product.controller;

import static org.springframework.http.HttpStatus.*;

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
import io.swagger.v3.oas.annotations.Parameter;
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
			""",
		parameters = {
			@Parameter(name = "keyword", description = "검색 키워드", example = "갤럭시"),
			@Parameter(name = "categoryIds", description = "카테고리 ID 목록(,로 구분)", example = "1,2,3"),
			@Parameter(name = "minPrice", description = "최소 가격", example = "100000"),
			@Parameter(name = "maxPrice", description = "최대 가격", example = "500000"),
			@Parameter(name = "sellerCode", description = "판매자 코드", example = "USER"),
			@Parameter(name = "productStatus", description = "상품 상태", example = "AVAILABLE"),
			@Parameter(name = "sortBy", description = "정렬 기준(price/title/createdAt/updatedAt", example = "price"),
			@Parameter(name = "sortDirection", description = "정렬 방향(ASC/DESC)", example = "ASC"),
			@Parameter(name = "page", description = "페이지 번호(1부터 시작)", example = "1"),
			@Parameter(name = "size", description = "페이지 크기(최대 100)", example = "10")
		}
	)
	public BaseResponse<PageDto<ProductSearchResponse>> searchProducts(ProductSearchRequest request) {

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
			
			- Title 및 CategoryName 기준 prefix에 따른 자동완성
			- 삭제된 상품은 제외
			- 카테고리별 자동완성(CategoryId) 지원
			""",
		parameters = {
			@Parameter(name = "keyword", description = "검색 키워드", example = "갤럭시"),
			@Parameter(name = "categoryId", description = "카테고리 ID", example = "1"),
			@Parameter(name = "includeSold", description = "판매 완료 상품 포함 여부", example = "false"),
			@Parameter(name = "size", description = "추천 결과 개수", example = "10"),
		}
	)
	public BaseResponse<ProductSuggestResponse> suggestCompletion(ProductSuggestRequest request) {

		return BaseResponse.success(
			OK.value(),
			productSearchService.suggestCompletion(request),
			"검색어 추천이 성공적으로 완료되었습니다."
		);
	}

	@GetMapping("/suggest/related")
	@Operation(summary = "연관 검색어 추천 By Elasticsearch",
		description = """
			Elasticsearch Suggest를 이용한 연관 검색어 추천 API입니다.
			
			- Title, CategoryName, Description에서 Keyword와 함께 자주 등장하는 단어 집계
			- 삭제된 상품 및 기본값 기준 판매 완료 상품 제외
			""",
		parameters = {
			@Parameter(name = "keyword", description = "검색 키워드", example = "갤럭시"),
			@Parameter(name = "categoryId", description = "카테고리 ID", example = "1"),
			@Parameter(name = "includeSold", description = "판매 완료 상품 포함 여부", example = "false"),
			@Parameter(name = "size", description = "추천 결과 개수", example = "10"),
		}
	)
	public BaseResponse<ProductSuggestResponse> suggestRelated(ProductSuggestRequest request) {

		return BaseResponse.success(
			OK.value(),
			productSearchService.suggestRelated(request),
			"검색어 추천이 성공적으로 완료되었습니다."
		);
	}
}
