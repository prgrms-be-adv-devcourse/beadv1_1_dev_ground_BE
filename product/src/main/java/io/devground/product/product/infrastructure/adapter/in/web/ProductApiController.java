package io.devground.product.product.infrastructure.adapter.in.web;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.domain.vo.request.ProductImageUrlsDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductDto;
import io.devground.product.product.domain.port.in.ProductUseCase;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.pagination.PageQuery;
import io.devground.product.product.domain.vo.response.CartProductsResponse;
import io.devground.product.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.product.domain.vo.response.ProductDetailResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.domain.vo.response.UpdateProductResponse;
import io.devground.product.product.infrastructure.model.web.request.CartProductsRequest;
import io.devground.product.product.infrastructure.model.web.request.ProductImageUrlsRequest;
import io.devground.product.product.infrastructure.model.web.request.RegistProductRequest;
import io.devground.product.product.infrastructure.model.web.request.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "ProductController", description = "상품 API")
public class ProductApiController {

	private final ProductUseCase productApplication;

	// TODO: sellerCode 정책 정해진 후 수정
	@GetMapping
	@Operation(summary = "상품 목록 조회", description = "DB에서 상품 목록을 반환합니다. 페이징, 정렬을 사용합니다.")
	public BaseResponse<PageDto<GetAllProductsResponse>> getProducts(PageQuery pageRequest) {

		return BaseResponse.success(
			OK.value(),
			productApplication.getProducts(pageRequest),
			"상품 목록이 성공적으로 조회되었습니다."
		);
	}

	@GetMapping("/user")
	@Operation(summary = "사용자 등록 상품 목록 조회", description = "DB에서 사용자가 등록한 상품 목록을 반환합니다. 페이징, 정렬을 사용합니다.")
	public BaseResponse<PageDto<GetAllProductsResponse>> getUserProducts(
		PageQuery pageRequest,
		@RequestHeader("X-CODE") String sellerCode
	) {

		return BaseResponse.success(
			OK.value(),
			productApplication.getUserProducts(sellerCode, pageRequest),
			"사용자가 등록한 상품 목록이 성공적으로 조회되었습니다."
		);
	}

	// TODO: sellerCode 정책 정해진 후 수정
	@PostMapping
	@Operation(summary = "상품 등록",
		description = "모든 유저는 상품을 등록할 수 있습니다. PresignedUrl 목록을 함께 반환합니다.")
	public BaseResponse<RegistProductResponse> registProduct(
		@RequestBody RegistProductRequest request,
		@RequestHeader("X-CODE") String sellerCode
	) {

		RegistProductDto requestDto = new RegistProductDto(
			request.categoryId(),
			request.title(),
			request.description(),
			request.price(),
			request.imageExtensions()
		);

		return BaseResponse.success(
			CREATED.value(),
			productApplication.registProduct(sellerCode, requestDto),
			"상품이 성공적으로 등록되었습니다."
		);
	}

	@PostMapping("/{productCode}/images/upload")
	@Operation(summary = "상품 이미지 등록", description = "상품 이미지 접근 가능 주소를 저장합니다.")
	public BaseResponse<Void> saveImageUrls(
		@PathVariable String productCode,
		@RequestBody ProductImageUrlsRequest request,
		@RequestHeader("X-CODE") String sellerCode
	) {

		ProductImageUrlsDto requestDto = new ProductImageUrlsDto(request.urls());

		return BaseResponse.success(
			NO_CONTENT.value(),
			productApplication.saveImageUrls(sellerCode, productCode, requestDto),
			"상품 이미지가 성공적으로 등록되었습니다."
		);
	}

	// TODO: sellerCode 정책 정해진 후 수정
	@GetMapping("/{productCode}")
	@Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회할 수 있습니다.")
	public BaseResponse<ProductDetailResponse> getProductDetail(
		@PathVariable String productCode,
		@RequestHeader("X-CODE") String userCode
	) {

		return BaseResponse.success(
			OK.value(),
			productApplication.getProductDetail(userCode, productCode),
			"상품 상세 정보를 성공적으로 불러왔습니다."
		);
	}

	// TODO: sellerCode 정책 정해진 후 수정
	@PatchMapping("/{productCode}")
	@Operation(summary = "상품 수정", description = "유저는 자신의 상품 판매 정보를 수정할 수 있습니다.")
	public BaseResponse<UpdateProductResponse> updateProduct(
		@PathVariable String productCode,
		@RequestBody UpdateProductRequest request,
		@RequestHeader(name = "X-CODE") String sellerCode
	) {

		UpdateProductDto requestDto = new UpdateProductDto(
			request.title(), request.description(), request.price(), request.deleteUrls(), request.newImageExtensions()
		);

		return BaseResponse.success(
			OK.value(),
			productApplication.updateProduct(sellerCode, productCode, requestDto),
			"상품 정보가 성공적으로 수정되었습니다."
		);
	}

	// TODO: sellerCode 정책 정해진 후 수정
	@DeleteMapping("/{productCode}")
	@Operation(summary = "상품 삭제", description = "유저는 자신의 상품 판매 정보를 삭제할 수 있습니다.")
	public BaseResponse<Void> deleteProduct(
		@PathVariable String productCode,
		@RequestHeader(value = "X-CODE") String sellerCode
	) {

		return BaseResponse.success(
			NO_CONTENT.value(),
			productApplication.deleteProduct(sellerCode, productCode),
			"상품 정보가 성공적으로 삭제되었습니다."
		);
	}

	@PostMapping("/carts")
	@Operation(summary = "장바구니의 상품 목록 조회", description = "장바구니에 등록된 상품 목록을 조회합니다.")
	public BaseResponse<List<CartProductsResponse>> getCartProducts(
		@RequestBody CartProductsRequest request
	) {

		CartProductsDto requestDto = new CartProductsDto(request.productCodes());

		return BaseResponse.success(
			OK.value(),
			productApplication.getCartProducts(requestDto),
			"장바구니의 상품 정보들을 성공적으로 불러왔습니다."
		);
	}

	@PatchMapping("/status")
	@Operation(summary = "상품 판매 처리", description = "상품의 상태를 판매된 상태로 변경합니다.")
	public BaseResponse<Void> updateStatusToSold(
		@RequestHeader("X-CODE") String sellerCode,
		@RequestBody CartProductsRequest request
	) {

		CartProductsDto requestDto = new CartProductsDto(request.productCodes());

		return BaseResponse.success(
			NO_CONTENT.value(),
			productApplication.updateStatusToSold(sellerCode, requestDto),
			"상품이 판매 완료 상태로 성공적으로 처리되었습니다."
		);
	}
}
