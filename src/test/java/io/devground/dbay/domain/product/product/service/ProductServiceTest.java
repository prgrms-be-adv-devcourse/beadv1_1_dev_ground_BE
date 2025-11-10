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
import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;
import io.devground.dbay.domain.product.category.service.CategoryService;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
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

	List<String> productCodes = new ArrayList<>();
	List<String> productSaleCodes = new ArrayList<>();
	List<Long> categoryIds = new ArrayList<>();

	@BeforeEach
	void init() {

		productCodes.clear();
		productSaleCodes.clear();
		categoryIds.clear();

		AdminCategoryResponse responseDepth1 = categoryService.registCategory(
			new RegistCategoryRequest("핸드폰", null));

		AdminCategoryResponse responseDepth2 = categoryService.registCategory(
			new RegistCategoryRequest("아이폰", responseDepth1.id()));

		AdminCategoryResponse responseDepth3 = categoryService.registCategory(
			new RegistCategoryRequest("아이폰15", responseDepth2.id()));

		categoryIds.addAll(List.of(responseDepth1.id(), responseDepth2.id(), responseDepth3.id()));

		String tempCode1 = "tempSellerCode";
		RegistProductRequest request1 = new RegistProductRequest
			(responseDepth3.id(), "갤럭시 팔아요", "이런 갤럭시입니다.", 300000L);

		RegistProductRequest request2 = new RegistProductRequest
			(responseDepth3.id(), "맥북 팔아요", "이런 맥북입니다.", 500000L);

		String otherCode = "otherSellerCode";
		RegistProductRequest request3 = new RegistProductRequest
			(responseDepth3.id(), "에어팟 팔아요", "이런 에어팟입니다.", 40000L);

		RegistProductResponse response1 = productService.registProduct(tempCode1, request1);
		RegistProductResponse response2 = productService.registProduct(tempCode1, request2);
		RegistProductResponse response3 = productService.registProduct(otherCode, request3);

		productCodes.addAll(List.of(response1.productCode(), response2.productCode(), response3.productCode()));
		productSaleCodes.addAll(
			List.of(response1.productSaleCode(), response2.productSaleCode(), response3.productSaleCode()));
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

		Product product = productRepository.findByCode(response.productCode()).get();
		ProductSale productSale = productSaleRepository.findByCode(response.productSaleCode()).get();

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

	@Test
	@DisplayName("성공_상품 정보 수정")
	void success_update_product() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		Product product = productRepository.findByCode(productCodes.getFirst()).get();
		UpdateProductRequest request = new UpdateProductRequest("새로운 제목", "새로운 설명", 1000000L);

		// when
		UpdateProductResponse response = productService.updateProduct(sellerCode, product.getCode(), request);
		Product updatedProduct = productRepository.findByCode(response.productCode()).get();

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
		UpdateProductRequest request = new UpdateProductRequest("새로운 제목", "새로운 설명", 1000000L);

		// when, then
		assertThrows(ServiceException.class,
			() -> productService.updateProduct(sellerCode, "wrongCode", request));
	}

	@Test
	@DisplayName("성공_상품 정보 삭제")
	void success_delete_product() throws Exception {

		// given
		String sellerCode = "tempSellerCode";
		String productCode = productCodes.getFirst();

		// when
		productService.deleteProduct(productCode);

		Product product = productRepository.findByCode(productCode).get();

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
		assertThrows(ServiceException.class,
			() -> productService.deleteProduct("wrongCode"));
	}
}