package io.devground.product.product.infrastructure.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.product.product.application.service.AdminCategoryApplication;
import io.devground.product.product.application.service.ProductApplicationService;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.vo.ProductRecommendSpec;
import io.devground.product.product.domain.vo.ProductStatus;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.client.ImageClient;
import io.devground.product.product.infrastructure.mapper.ProductMapper;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProductVectorUtilTest {

	@Autowired
	ProductApplicationService productService;

	@Autowired
	AdminCategoryApplication categoryService;

	@Autowired
	ProductJpaRepository productRepository;

	@MockBean
	ImageClient imageClient;

	Product product;
	Long categoryId;
	String productCode;

	static final String SELLER_CODE = "seller";
	static final String PRODUCT_CODE = "productCode";
	static final String TEST_THUMBNAIL_URL = "https://placehold.co/600x400/jpg";

	@BeforeEach
	void setUp() {
		AdminCategoryResponse category = categoryService.registCategory(
			new RegistCategoryDto("전자기기", null)
		);
		categoryId = category.id();

		RegistProductDto request = new RegistProductDto(
			categoryId, "아이폰 15 Pro", "최신 아이폰입니다.", 1500000L, null
		);

		RegistProductResponse response = productService.registProduct(SELLER_CODE + "1", request);
		productCode = response.productCode();

		ProductEntity productEntity = productRepository.findByCode(productCode).orElseThrow();
		product = ProductMapper.toProductDomain(productEntity, productEntity.getProductSale());
	}

	@Test
	@DisplayName("성공_벡터 Document로 변환 - metadata")
	void success_embed_doc_metadata() throws Exception {

		// given, when
		Document document = ProductVectorUtil.toVectorDocument(product);

		// then
		Map<String, Object> metadata = document.getMetadata();

		assertEquals(productCode, metadata.get("productCode"));
		assertEquals("전자기기", metadata.get("categoryFullPath"));
		assertEquals("아이폰 15 Pro", metadata.get("title"));
		assertEquals("최신 아이폰입니다.", metadata.get("description"));
		assertEquals(1500000L, metadata.get("price"));
		assertEquals(ProductStatus.ON_SALE.name(), metadata.get("productStatus"));
		assertEquals(DeleteStatus.N.name(), metadata.get("deleteStatus"));
	}

	@Test
	@DisplayName("성공_벡터 Document로 변환 - ID가 productCode")
	void success_embed_id_equals_productCode() throws Exception {

		// given, when
		Document document = ProductVectorUtil.toVectorDocument(product);

		// then
		assertEquals(productCode, document.getId());
	}

	@Test
	@DisplayName("성공_벡터 Document 변환 - ProductRecommendSpec")
	void success_doc_to_recommend_spec() throws Exception {

		// given
		Map<String, Object> metadata = Map.of(
			"productCode", PRODUCT_CODE + "1",
			"title", "아이폰 15 Pro",
			"description", "최신 아이폰입니다.",
			"categoryFullPath", "전자기기",
			"thumbnailUrl", TEST_THUMBNAIL_URL,
			"price", 1500000L,
			"productStatus", ProductStatus.ON_SALE.name()
		);

		Document document = new Document(PRODUCT_CODE + "1", "content", metadata);

		// when
		ProductRecommendSpec response = ProductVectorUtil.toRecommendSpec(document);

		// then
		assertEquals(PRODUCT_CODE + "1", response.productCode());
		assertEquals("아이폰 15 Pro", response.title());
		assertEquals("최신 아이폰입니다.", response.description());
		assertEquals("전자기기", response.categoryFullPath());
		assertEquals(TEST_THUMBNAIL_URL, response.thumbnailUrl());
		assertEquals(1500000L, response.price());
		assertEquals(ProductStatus.ON_SALE.name(), response.productStatus());
	}

	@Test
	@DisplayName("성공_벡터 Document 변환 - metadata가 없을 때 ProductRecommendSpec")
	void success_doc_to_recommend_spec_without_metadata() throws Exception {

		// given
		Document document = new Document(PRODUCT_CODE + "1", "content", Map.of());

		// when
		ProductRecommendSpec response = ProductVectorUtil.toRecommendSpec(document);

		// then
		assertEquals(PRODUCT_CODE + "1", response.productCode());
		assertEquals("", response.title());
		assertEquals("", response.description());
		assertEquals("", response.categoryFullPath());
		assertEquals("", response.thumbnailUrl());
		assertEquals(0L, response.price());
		assertEquals(ProductStatus.ON_SALE.name(), response.productStatus());
	}

	@Test
	@DisplayName("성공_벡터 Document 변환 - price가 Integer일 때 ProductRecommendSpec")
	void success_doc_to_recommend_spec_price_integer() throws Exception {

		// given
		Map<String, Object> metadata = Map.of("price", 1500000);
		Document document = new Document(PRODUCT_CODE + "1", "content", metadata);

		// when
		ProductRecommendSpec response = ProductVectorUtil.toRecommendSpec(document);

		// then
		assertEquals(PRODUCT_CODE + "1", response.productCode());
		assertEquals(1500000L, response.price());
	}

	@Test
	@DisplayName("성공_Product 변환 - ProductRecommendSpec")
	void success_product_to_recommend_spec() throws Exception {

		// given, when
		ProductRecommendSpec response = ProductVectorUtil.toRecommendSpec(product);

		// then
		assertEquals(productCode, response.productCode());
		assertEquals("아이폰 15 Pro", response.title());
		assertEquals("최신 아이폰입니다.", response.description());
		assertEquals("전자기기", response.categoryFullPath());
		assertEquals(1500000L, response.price());
		assertEquals(ProductStatus.ON_SALE.name(), response.productStatus());
	}

	@Test
	@DisplayName("성공_사용자 조회 상품 기반 쿼리 생성 - 단일 상품")
	void success_create_single_product_query_by_user_view() throws Exception {

		// given
		List<Product> products = List.of(product);

		// when
		String query = ProductVectorUtil.createQueryFromUserViewedProducts(products);

		// then
		String expectedQuery = """
			카테고리: 전자기기
			상품명: 아이폰 15 Pro
			설명: 최신 아이폰입니다.
			""";

		assertEquals(expectedQuery, query);
	}

	@Test
	@DisplayName("성공_사용자 조회 상품 기반 쿼리 생성 - 다중 상품")
	void success_create_multi_product_query_by_user_view() throws Exception {

		// given
		AdminCategoryResponse category = categoryService.registCategory(
			new RegistCategoryDto("가전", null)
		);

		RegistProductDto productDto = new RegistProductDto(
			category.id(), "맥북 Pro", "최신 맥북입니다.", 2000000L, null
		);

		RegistProductResponse productResponse = productService.registProduct(SELLER_CODE, productDto);

		ProductEntity entity = productRepository.findByCode(productResponse.productCode()).get();
		Product product2 = ProductMapper.toProductDomain(entity, entity.getProductSale());

		List<Product> products = List.of(product, product2);

		// when
		String query = ProductVectorUtil.createQueryFromUserViewedProducts(products);

		// then
		String expectedQuery = """
			카테고리: 전자기기
			상품명: 아이폰 15 Pro
			설명: 최신 아이폰입니다.
			
			카테고리: 가전
			상품명: 맥북 Pro
			설명: 최신 맥북입니다.
			""";

		assertEquals(expectedQuery, query);
	}

	@Test
	@DisplayName("성공_상품 상세 기반 쿼리 생성")
	void success_create_query_by_product_detail() throws Exception {

		// given, when
		String query = ProductVectorUtil.createQueryFromProductDetail(product);

		// then
		String expectedQuery = """
			카테고리: 전자기기
			상품명: 아이폰 15 Pro
			설명: 최신 아이폰입니다.
			""";

		assertEquals(expectedQuery, query);
	}
}