package io.devground.product.product.infrastructure.adapter.in.web;

import static org.springframework.http.HttpStatus.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.domain.port.in.ProductRecommendUseCase;
import io.devground.product.product.domain.vo.response.ProductRecommendResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/recommend")
@Tag(name = "ProductRecommendController", description = "상품 추천 API")
public class ProductRecommendApiController {

	private final ProductRecommendUseCase productRecommendUseCase;

	@GetMapping("/user-view")
	@Operation(summary = "사용자 조회 기반 상품 추천",
		description = """
			로그인한 사용자의 최근 조회 이력을 기반으로 벡터스토어를 통해 유사 상품을 추천합니다.
			
			- 로그인 + 조회 5건 이상 => 사용자 조회 기반 상품 추천
			- 로그인 + 조회 5건 미만 or 비로그인 => 인기순 상품 추천(폴백)
			""")
	public BaseResponse<ProductRecommendResponse> recommendByUserView(
		@RequestHeader(value = "X-CODE", required = false) String userCode,
		@RequestParam(name = "size", required = false, defaultValue = "10") Integer size
	) {

		return BaseResponse.success(
			OK.value(),
			productRecommendUseCase.recommendByUserView(userCode, size),
			"유저 조회 기반 상품 추천이 성공적으로 완료되었습니다."
		);
	}

	@GetMapping("/{productCode}")
	@Operation(summary = "조회 상품 기반 상품 추천", description = "현재 상품의 카테고리/상품명/설명 기반으로 벡터스토어를 통해 유사 상품을 추천합니다.")
	public BaseResponse<ProductRecommendResponse> recommendByProductDetail(
		@PathVariable String productCode,
		@RequestParam(name = "size", required = false, defaultValue = "10") Integer size
	) {

		return BaseResponse.success(
			OK.value(),
			productRecommendUseCase.recommendByProductDetail(productCode, size),
			"상품 상세 기반 상품 추천이 성공적으로 완료되었습니다."
		);
	}
}
