package io.devground.dbay.domain.product.product.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.product.product.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
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

	// TODO: sellerCode 정책 정해진 후 수정
	@PatchMapping("{productCode}")
	@Operation(summary = "상품 수정", description = "유저는 자신의 상품 판매 정보를 수정할 수 있습니다.")
	public BaseResponse<UpdateProductResponse> updateProduct(
		@PathVariable String productCode,
		@RequestBody UpdateProductRequest request,
		@RequestHeader(name = "X-CODE") String sellerCode
	) {

		return BaseResponse.success(
			OK.value(),
			productService.updateProduct(sellerCode, productCode, request),
			"상품 정보가 성공적으로 수정되었습니다."
		);
	}

	// TODO: sellerCode 정책 정해진 후 수정
	@DeleteMapping("{productCode}")
	@ResponseStatus(NO_CONTENT)
	@Operation(summary = "상품 삭제", description = "유저는 자신의 상품 판매 정보를 삭제할 수 있습니다.")
	public void deleteProduct(@PathVariable String productCode) {

		productService.deleteProduct(productCode);
	}

	@PostMapping("/carts")
	@Operation(summary = "장바구니의 상품 목록 조회", description = "장바구니에 등록된 상품 목록을 조회합니다.")
	public BaseResponse<List<CartProductsResponse>> getCartProducts(
		@RequestBody CartProductsRequest request
	) {

		return BaseResponse.success(
			OK.value(),
			productService.getCartProducts(request),
			"장바구니의 상품 정보들을 성공적으로 불러왔습니다."
		);
	}
}
