package io.devground.product.product.application.service;

import static io.devground.product.product.domain.vo.ProductStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.pagination.PageQuery;
import io.devground.product.product.domain.vo.pagination.SortSpec;
import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.CartProductsResponse;
import io.devground.product.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.product.domain.vo.response.ProductDetailResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.domain.vo.response.UpdateProductResponse;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductSaleJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.client.ImageClient;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.model.persistence.ProductSaleEntity;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProductApplicationServiceTest {

	@Autowired
	ProductApplicationService productService;
	@Autowired
	ProductJpaRepository productRepository;
	@Autowired
	ProductSaleJpaRepository productSaleRepository;
	@Autowired    // 카테고리 Init 용, 이외 사용 금지
	AdminCategoryApplication categoryService;

	@MockBean
	ImageClient imageClient;

	List<String> productCodes = new ArrayList<>();
	List<String> productSaleCodes = new ArrayList<>();
	List<String> tempSellerProductCodes = new ArrayList<>();
	List<Long> categoryIds = new ArrayList<>();

	String tempSellerCode = "tempSellerCode";
	String otherSellerCode = "otherSellerCode";

	@BeforeEach
	void init() {

		productCodes.clear();
		productSaleCodes.clear();
		categoryIds.clear();

		AdminCategoryResponse responseDepth1 = categoryService.registCategory(
			new RegistCategoryDto("핸드폰", null));

		AdminCategoryResponse responseDepth2 = categoryService.registCategory(
			new RegistCategoryDto("아이폰", responseDepth1.id()));

		AdminCategoryResponse responseDepth3 = categoryService.registCategory(
			new RegistCategoryDto("아이폰15", responseDepth2.id()));

		categoryIds.addAll(List.of(responseDepth1.id(), responseDepth2.id(), responseDepth3.id()));

		RegistProductDto request1 = new RegistProductDto
			(responseDepth3.id(), "갤럭시 팔아요", "이런 갤럭시입니다.", 600000L, null);

		RegistProductDto request2 = new RegistProductDto
			(responseDepth3.id(), "맥북 팔아요", "이런 맥북입니다.", 500000L, null);

		RegistProductDto request3 = new RegistProductDto
			(responseDepth3.id(), "에어팟 팔아요", "이런 에어팟입니다.", 300000L, null);

		RegistProductResponse response1 = productService.registProduct(tempSellerCode, request1);
		RegistProductResponse response2 = productService.registProduct(tempSellerCode, request2);
		RegistProductResponse response3 = productService.registProduct(otherSellerCode, request3);

		productCodes.addAll(List.of(response1.productCode(), response2.productCode(), response3.productCode()));
		productSaleCodes.addAll(
			List.of(response1.productSaleCode(), response2.productSaleCode(), response3.productSaleCode()));
		tempSellerProductCodes.addAll(List.of(response1.productCode(), response2.productCode()));
	}

	@Test
	@DisplayName("성공_상품 등록")
	void success_regist_product() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		RegistProductDto request = new RegistProductDto
			(categoryIds.getLast(), "아이폰 팔아요", "이런 아이폰입니다.", 300000L, null);

		// when
		RegistProductResponse response = productService.registProduct(sellerCode, request);

		ProductEntity product = productRepository.findByCode(response.productCode()).get();
		ProductSaleEntity productSale = productSaleRepository.findByCode(response.productSaleCode()).get();

		// then
		assertEquals(request.title(), product.getTitle());
		assertEquals(request.description(), product.getDescription());
		assertEquals(sellerCode, productSale.getSellerCode());
	}

	// TODO: 상품 등록 시 sellerCode 검증

	@Test
	@DisplayName("실퍠_상품 등록 시 하위 카테고리 존재")
	void fail_regist_product_non_leaf_category() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		RegistProductDto request = new RegistProductDto(
			categoryIds.getFirst(), "아이폰 팔아요", "이런 아이폰입니다.", 300000L, null);

		// when, then
		assertThrows(ProductDomainException.class,
			() -> productService.registProduct(sellerCode, request));
	}

	@Test
	@DisplayName("실패_상품 등록 시 카테고리 오입력")
	void fail_regist_product_wrong_category() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		RegistProductDto request = new RegistProductDto(
			10000L, "아이폰 팔아요", "이런 아이폰입니다.", 300000L, null);

		// when, then
		assertThrows(ProductDomainException.class,
			() -> productService.registProduct(sellerCode, request));
	}

	@Test
	@DisplayName("성공_상품 목록 조회")
	void success_get_all_products() throws Exception {

		// given
		PageQuery pageRequest = new PageQuery(1, 10, new SortSpec("createdAt", SortSpec.Direction.DESC));

		// when
		PageDto<GetAllProductsResponse> productsWithinPageDto = productService.getProducts(pageRequest);
		List<GetAllProductsResponse> products = productsWithinPageDto.items();

		// then
		assertEquals(3, products.size());

		for (GetAllProductsResponse product : products) {
			assertThat(productCodes)
				.contains(product.productCode());
		}
	}

	@Test
	@DisplayName("성공_상품 목록 조회 시 잘못된 Pageable")
	void success_get_all_products_with_wrong_format_pageable() throws Exception {

		// given
		PageQuery pageRequest = new PageQuery(1, 10, new SortSpec("tempSort", SortSpec.Direction.DESC));

		// when
		PageDto<GetAllProductsResponse> productsWithinPageDto = productService.getProducts(pageRequest);
		List<GetAllProductsResponse> products = productsWithinPageDto.items();

		// then
		assertEquals(3, products.size());

		for (int i = 0; i < products.size(); i++) {
			assertEquals(productCodes.get(products.size() - i - 1), products.get(i).productCode());
		}
	}

	@Test
	@DisplayName("성공_상품 목록 조회 시 2개씩 조회")
	void success_get_all_products_with_less_page_size() throws Exception {

		// given
		PageQuery pageRequest = new PageQuery(1, 2, new SortSpec("createdAt", SortSpec.Direction.DESC));

		// when
		PageDto<GetAllProductsResponse> productsWithinPageDto = productService.getProducts(pageRequest);
		List<GetAllProductsResponse> products = productsWithinPageDto.items();

		// then
		assertEquals(2, products.size());

		for (int i = 0; i < products.size(); i++) {
			assertEquals(productCodes.get(products.size() - i), products.get(i).productCode());
		}
	}

	@Test
	@DisplayName("성공_상품 목록 조회 시 0개 조회")
	void success_get_all_products_no_data() throws Exception {

		// given
		PageQuery pageRequest = new PageQuery(10, 2, new SortSpec("createdAt", SortSpec.Direction.DESC));

		// when
		PageDto<GetAllProductsResponse> productsWithinPageDto = productService.getProducts(pageRequest);
		List<GetAllProductsResponse> products = productsWithinPageDto.items();

		// then
		assertThat(products).isEmpty();
	}

	@Test
	@DisplayName("성공_상품 목록 조회 시 price로 정렬")
	void success_get_all_products_sort_by_price() throws Exception {

		// 가격 내림차순

		// given
		PageQuery pageRequest = new PageQuery(1, 10, new SortSpec("price", SortSpec.Direction.DESC));

		// when
		PageDto<GetAllProductsResponse> productsWithinPageDto = productService.getProducts(pageRequest);
		List<GetAllProductsResponse> products = productsWithinPageDto.items();

		// then
		assertEquals(3, products.size());

		for (int i = 0; i < products.size(); i++) {
			assertEquals(productCodes.get(i), products.get(i).productCode());
		}

		// 가격 오름차순

		// given
		pageRequest = new PageQuery(1, 10, new SortSpec("price", SortSpec.Direction.ASC));

		// when
		productsWithinPageDto = productService.getProducts(pageRequest);
		products = productsWithinPageDto.items();

		// then
		assertEquals(3, products.size());

		for (int i = 0; i < products.size(); i++) {
			assertEquals(productCodes.get(products.size() - i - 1), products.get(i).productCode());
		}
	}

	@Test
	@DisplayName("성공_상품 상세 조회")
	void success_get_product_detail() throws Exception {

		// given
		String sellerCode = "tempSellerCode";

		// when
		given(imageClient.getImageUrls(anyString(), any()))
			.willReturn(BaseResponse.success(200, List.of("")));

		ProductDetailResponse product = productService.getProductDetail(sellerCode, productCodes.getFirst());

		// then
		assertEquals(sellerCode, product.sellerCode());
		assertEquals("갤럭시 팔아요", product.title());
		assertEquals("이런 갤럭시입니다.", product.description());
		assertEquals("핸드폰/아이폰/아이폰15", product.categoryPath());
		assertEquals(600000L, product.price());
		assertEquals(ON_SALE, product.productStatus());
	}

	@Test
	@DisplayName("실패_상품 상세 조회 시 잘못된 상품 코드")
	void fail_get_product_detail_wrong_product_code() throws Exception {

		// given
		String productCode = "wrongCode";

		// when, then
		assertThrows(ProductDomainException.class,
			() -> productService.getProductDetail("userCode", productCode));
	}

	@Test
	@DisplayName("성공_상품 정보 수정")
	void success_update_product() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		ProductEntity product = productRepository.findByCode(productCodes.getFirst()).get();
		UpdateProductDto request = new UpdateProductDto("새로운 제목", "새로운 설명", 1000000L, null, null);

		// when
		UpdateProductResponse response = productService.updateProduct(sellerCode, product.getCode(), request);
		ProductEntity updatedProduct = productRepository.findByCode(response.productCode()).get();

		// then
		assertEquals("새로운 제목", updatedProduct.getTitle());
		assertEquals("새로운 설명", updatedProduct.getDescription());
		assertEquals(1000000L, updatedProduct.getProductSale().getPrice());
	}

	// TODO: 상품 수정 - 인가 실패

	@Test
	@DisplayName("실패_상품 정보 수정 시 상품 코드 오입력")
	void fail_update_product_wrong_product_code() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		UpdateProductDto request = new UpdateProductDto("새로운 제목", "새로운 설명", 1000000L, null, null);

		// when, then
		assertThrows(ProductDomainException.class,
			() -> productService.updateProduct(sellerCode, "wrongCode", request));
	}

	@Test
	@DisplayName("성공_상품 정보 삭제")
	void success_delete_product() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		String productCode = productCodes.getFirst();

		// when
		productService.deleteProduct(sellerCode, productCode);

		ProductEntity product = productRepository.findByCode(productCode).get();

		// then
		assertEquals(DeleteStatus.Y, product.getDeleteStatus());
	}

	// TODO: 상품 삭제 - 인가 실패

	@Test
	@DisplayName("실퍠_상품 삭제 시 상품 코드 오입력")
	void fail_delete_product_wrong_product_code() throws Exception {

		// given
		String sellerCode = "tempSellerCode";

		// when, then
		assertThrows(ProductDomainException.class,
			() -> productService.deleteProduct("wrongCode", sellerCode));
	}

	@Test
	@DisplayName("성공_장바구니 상품 목록 조회")
	void success_get_cart_products() throws Exception {

		// given
		CartProductsDto request = new CartProductsDto(productCodes);

		// when
		List<CartProductsResponse> responses = productService.getCartProducts(request);
		List<ProductEntity> refreshedProducts = productRepository.findProductsByCodes(productCodes);

		// then
		assertEquals(refreshedProducts.size(), responses.size());

		for (int i = 0; i < refreshedProducts.size(); i++) {
			ProductEntity p = refreshedProducts.get(i);
			CartProductsResponse cpResponse = responses.get(i);

			assertEquals(p.getTitle(), cpResponse.title());
			assertEquals(p.getDescription(), cpResponse.description());
			assertEquals(p.getCategory().getName(), cpResponse.categoryName());
			assertEquals(p.getProductSale().getPrice(), cpResponse.price());
			assertEquals(p.getProductSale().getSellerCode(), cpResponse.sellerCode());
		}
	}

	@Test
	@DisplayName("실패_장바구니 상품 목록 조회 시 잘못된 상품 코드 포함")
	void fail_get_cart_products_wrong_product_code() throws Exception {

		// given
		productCodes.add("wrongCode");
		CartProductsDto request = new CartProductsDto(productCodes);

		// when, then
		assertThrows(ServiceException.class,
			() -> productService.getCartProducts(request));
	}

	@Test
	@DisplayName("실패_장바구니 상품 목록 조회 시 상품 코드 미존재")
	void fail_get_cart_products_non_product_code() throws Exception {

		// given
		CartProductsDto request = new CartProductsDto(List.of());

		// when, then
		assertThrows(ServiceException.class,
			() -> productService.getCartProducts(request));
	}

	@Test
	@DisplayName("성공_주문된 상품 목록 상태 판매 처리")
	void success_products_to_sold() throws Exception {

		// given
		CartProductsDto request = new CartProductsDto(tempSellerProductCodes);

		// when
		productService.updateStatusToSold(tempSellerCode, request);
		List<ProductEntity> products = productRepository.findProductsByCodes(tempSellerProductCodes);

		for (ProductEntity p : products) {
			assertEquals(SOLD, p.getProductSale().getProductStatus());
		}
	}

	@Test
	@DisplayName("실패_주문된 상품 목록 판매 처리 중 이미 판매된 상품 존재")
	void fail_products_to_sold_already_sold() throws Exception {

		// given
		String firstCode = tempSellerProductCodes.getFirst();
		ProductEntity beforeProduct = productRepository.findByCode(firstCode).get();
		beforeProduct.getProductSale().updateProductStatus(SOLD);

		CartProductsDto request = new CartProductsDto(tempSellerProductCodes);

		// when, then
		assertThrows(ProductDomainException.class,
			() -> productService.updateStatusToSold(tempSellerCode, request));
	}
}