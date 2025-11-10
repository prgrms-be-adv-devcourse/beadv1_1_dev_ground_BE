package io.devground.dbay.domain.product.product.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "ProductController", description = "상품 API")
public class ProductController {

	private final ProductService productService;

	// TODO: sellerCode 정책 정해진 후 수정
	@PostMapping
	@Operation(summary = "상품 등록", description = "모든 유저는 상품을 등록할 수 있습니다.")
	public BaseResponse<RegistProductResponse> registProduct(
		@RequestBody RegistProductRequest request,
		@RequestHeader(name = "X-CODE") String sellerCode
	) {

		return BaseResponse.success(
			CREATED.value(),
			productService.registProduct(sellerCode, request),
			"상품이 성공적으로 등록되었습니다."
		);
	}
}
