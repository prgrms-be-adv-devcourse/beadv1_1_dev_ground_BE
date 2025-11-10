package io.devground.dbay.domain.product.product.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.exception.ServiceException;
import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;
import io.devground.dbay.domain.product.category.service.CategoryService;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;
import io.devground.dbay.domain.product.product.repository.ProductRepository;
import io.devground.dbay.domain.product.product.repository.ProductSaleRepository;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

	@Autowired
	ProductService productService;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductSaleRepository productSaleRepository;
	@Autowired    // 카테고리 Init 용, 이외 사용 금지
	CategoryService categoryService;

	List<Long> categoryIds = new ArrayList<>();

	@BeforeEach
	void init() {

		categoryIds.clear();

		AdminCategoryResponse responseDepth1 = categoryService.registCategory(
			new RegistCategoryRequest("핸드폰", null));

		AdminCategoryResponse responseDepth2 = categoryService.registCategory(
			new RegistCategoryRequest("아이폰", responseDepth1.id()));

		AdminCategoryResponse responseDepth3 = categoryService.registCategory(
			new RegistCategoryRequest("아이폰15", responseDepth2.id()));

		categoryIds.addAll(List.of(responseDepth1.id(), responseDepth2.id(), responseDepth3.id()));
	}

	@Test
	@DisplayName("성공_상품 등록")
	void success_regist_product() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		RegistProductRequest request = new RegistProductRequest
			(categoryIds.getLast(), "아이폰 팔아요", "이런 아이폰입니다.", 300000L);

		// when
		RegistProductResponse response = productService.registProduct(sellerCode, request);

		Product product = productRepository.findById(response.productId()).get();
		ProductSale productSale = productSaleRepository.findById(response.productId()).get();

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
		RegistProductRequest request = new RegistProductRequest(
			categoryIds.getFirst(), "아이폰 팔아요", "이런 아이폰입니다.", 300000L);

		// when, then
		assertThrows(ServiceException.class,
			() -> productService.registProduct(sellerCode, request));
	}

	@Test
	@DisplayName("실패_상품 등록 시 카테고리 오입력")
	void fail_regist_product_wrong_category() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		RegistProductRequest request = new RegistProductRequest(
			10000L, "아이폰 팔아요", "이런 아이폰입니다.", 300000L);

		// when, then
		assertThrows(ServiceException.class,
			() -> productService.registProduct(sellerCode, request));
	}
}