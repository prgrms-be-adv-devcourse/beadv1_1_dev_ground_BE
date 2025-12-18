package io.devground.product.product.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.product.product.domain.vo.RecommendType;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.ProductRecommendResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.infrastructure.adapter.out.ProductViewAdapter;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ProductRecommendIntegrationTest {

	@Autowired
	ProductRecommendApplication recommendService;
	@Autowired
	ProductApplicationService productService;
	@Autowired
	AdminCategoryApplication categoryService;
	@Autowired
	ProductViewAdapter productViewAdapter;
	@Autowired
	StringRedisTemplate redisTemplate;
	@MockBean
	VectorStore vectorStore;

	static final String PRODUCT_CODE = "productCode";

	Long categoryId;
	String userCode = "user";
	String sellerCode = "seller";

	@BeforeEach
	void setUp() {
		redisTemplate.execute((RedisConnection conn) -> {
			conn.serverCommands().flushAll();
			return null;
		});

		AdminCategoryResponse category = categoryService.registCategory(
			new RegistCategoryDto("전자기기", null)
		);
		categoryId = category.id();
	}

	@Test
	@DisplayName("성공_조회 이력 5개 이상 상품 추천")
	void success_rec_by_user_view_more_than_5() throws Exception {

		// given
		RegistProductResponse product1 = this.createProduct("아이폰 15 Pro", 1500000L);
		RegistProductResponse product2 = this.createProduct("아이폰 15", 1200000L);
		RegistProductResponse product3 = this.createProduct("아이패드 Pro", 1000000L);
		RegistProductResponse product4 = this.createProduct("맥북 Pro", 2000000L);
		RegistProductResponse product5 = this.createProduct("에어팟 Pro", 200000L);

		RegistProductResponse rec1 = this.createProduct("아이폰 14 Pro", 1300000L);
		RegistProductResponse rec2 = this.createProduct("아이패드 Air", 800000L);
		RegistProductResponse rec3 = this.createProduct("맥북 Air", 1800000L);

		productViewAdapter.saveView(userCode, product1.productCode());
		productViewAdapter.saveView(userCode, product2.productCode());
		productViewAdapter.saveView(userCode, product3.productCode());
		productViewAdapter.saveView(userCode, product4.productCode());
		productViewAdapter.saveView(userCode, product5.productCode());

		given(vectorStore.similaritySearch(any(SearchRequest.class)))
			.willReturn(List.of(
				this.createDocument(rec1.productCode(), "아이폰 14 Pro", 1300000L),
				this.createDocument(rec2.productCode(), "아이패드 Air", 800000L),
				this.createDocument(rec3.productCode(), "맥북 Air", 1800000L)
			));

		// when
		ProductRecommendResponse response = recommendService.recommendByUserView(userCode, 3);

		// then
		assertThat(response.recommendSpecs()).hasSize(3);
		assertEquals(RecommendType.USER_VIEW_HISTORY, response.recommendType());
		assertEquals("아이폰 14 Pro", response.recommendSpecs().getFirst().title());
		assertEquals("아이패드 Air", response.recommendSpecs().get(1).title());
		assertEquals("맥북 Air", response.recommendSpecs().get(2).title());
	}

	@Test
	@DisplayName("성공_비로그인 사용자는 폴백으로 인기 상품 추천")
	void success_rec_without_login_user_fallback_popular() throws Exception {

		// given
		RegistProductResponse product1 = this.createProduct("상품1", 100000L);
		RegistProductResponse product2 = this.createProduct("상품2", 200000L);
		RegistProductResponse product3 = this.createProduct("상품3", 300000L);
		RegistProductResponse product4 = this.createProduct("상품4", 400000L);
		RegistProductResponse product5 = this.createProduct("상품5", 500000L);

		this.increasePopularCount(product1.productCode(), 100);
		this.increasePopularCount(product2.productCode(), 80);
		this.increasePopularCount(product3.productCode(), 60);
		this.increasePopularCount(product4.productCode(), 40);
		this.increasePopularCount(product5.productCode(), 20);

		// when
		ProductRecommendResponse response = recommendService.recommendByUserView(null, 3);

		// then
		assertThat(response.recommendSpecs()).hasSize(3);
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());
		assertEquals("상품1", response.recommendSpecs().getFirst().title());
		assertEquals("상품2", response.recommendSpecs().get(1).title());
		assertEquals("상품3", response.recommendSpecs().get(2).title());
	}

	@Test
	@DisplayName("성공_조회 이력 5개 미만은 폴백으로 인기 상품 추천")
	void success_rec_less_than_5_fallback_popular() throws Exception {

		// given
		RegistProductResponse product1 = this.createProduct("상품1", 100000L);
		RegistProductResponse product2 = this.createProduct("상품2", 200000L);
		RegistProductResponse product3 = this.createProduct("상품3", 300000L);

		this.increasePopularCount(product1.productCode(), 50);
		this.increasePopularCount(product2.productCode(), 30);
		this.increasePopularCount(product3.productCode(), 10);

		productViewAdapter.saveView(userCode, product1.productCode());
		productViewAdapter.saveView(userCode, product2.productCode());
		productViewAdapter.saveView(userCode, product3.productCode());

		// when
		ProductRecommendResponse response = recommendService.recommendByUserView(userCode, 3);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());
		assertThat(response.recommendSpecs()).hasSize(3);
	}

	@Test
	@DisplayName("성공_상품 추천 결과가 없으면 폴백으로 인기 상품 추천")
	void success_rec_without_result_fallback_popular() throws Exception {

		// given
		RegistProductResponse product1 = this.createProduct("상품1", 100000L);
		RegistProductResponse product2 = this.createProduct("상품2", 200000L);

		this.increasePopularCount(product1.productCode(), 100);
		this.increasePopularCount(product2.productCode(), 50);

		for (int i = 0; i < 5; i++) {
			productViewAdapter.saveView(userCode, PRODUCT_CODE + i);
		}

		given(vectorStore.similaritySearch(any(SearchRequest.class))).willReturn(List.of());

		// when
		ProductRecommendResponse response = recommendService.recommendByUserView(userCode, 3);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());
		assertThat(response.recommendSpecs()).hasSize(2);
	}

	@Test
	@DisplayName("성공_인기 상품이 없으면 빈 결과 반환")
	void success_rec_without_popular_result() throws Exception {

		// given, when
		ProductRecommendResponse response = recommendService.recommendByUserView(null, 10);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());
		assertThat(response.recommendSpecs()).isEmpty();
	}

	@Test
	@DisplayName("성공_size 파라미터가 null이면 기본값인 10")
	void success_rec_without_size() throws Exception {

		// given
		for (int i = 1; i <= 15; i++) {
			RegistProductDto request = new RegistProductDto(categoryId, "상품" + i, "설명" + i, 100000L, null);
			RegistProductResponse response = productService.registProduct(sellerCode, request);
			productViewAdapter.increasePopularCount(response.productCode());
		}

		// when
		ProductRecommendResponse response = recommendService.recommendByUserView(null, null);

		// then
		assertThat(response.recommendSpecs()).hasSizeLessThanOrEqualTo(10);
	}

	@Test
	@DisplayName("성공_size 파라미터가 30을 초과하면 기본값인 10")
	void success_rec_exceed_max_size() throws Exception {

		// given
		for (int i = 1; i <= 15; i++) {
			RegistProductDto request = new RegistProductDto(categoryId, "상품" + i, "설명" + i, 100000L, null);
			RegistProductResponse response = productService.registProduct(sellerCode, request);
			productViewAdapter.increasePopularCount(response.productCode());
		}

		// when
		ProductRecommendResponse response = recommendService.recommendByUserView(null, 100);

		// then
		assertThat(response.recommendSpecs()).hasSizeLessThanOrEqualTo(10);
	}

	@Test
	@DisplayName("성공_상품 상세 기반 상품 추천 - 해당 상품 중복 제거 함께 확인")
	void success_rec_by_product_detail() throws Exception {

		// given
		RegistProductResponse product = this.createProduct("아이폰 15 Pro", 1500000L);

		RegistProductResponse rec1 = this.createProduct("아이폰 14 Pro", 1300000L);
		RegistProductResponse rec2 = this.createProduct("아이폰 15", 1200000L);
		RegistProductResponse rec3 = this.createProduct("아이폰 케이스", 50000L);

		given(vectorStore.similaritySearch(any(SearchRequest.class)))
			.willReturn(List.of(
				this.createDocument(product.productCode(), "아이폰 15 Pro", 1500000L),
				this.createDocument(rec1.productCode(), "아이폰 14 Pro", 1300000L),
				this.createDocument(rec2.productCode(), "아이폰 15", 1200000L),
				this.createDocument(rec3.productCode(), "아이폰 케이스", 50000L)
			));

		// when
		ProductRecommendResponse response = recommendService.recommendByProductDetail(product.productCode(), 3);

		// then
		assertThat(response.recommendSpecs()).hasSize(3);
		assertEquals(RecommendType.PRODUCT_DETAIL, response.recommendType());
		assertEquals("아이폰 14 Pro", response.recommendSpecs().getFirst().title());
		assertEquals("아이폰 15", response.recommendSpecs().get(1).title());
		assertEquals("아이폰 케이스", response.recommendSpecs().get(2).title());
	}

	private RegistProductResponse createProduct(String title, Long price) {
		RegistProductDto request = new RegistProductDto(categoryId, title, "설명: " + title, price, null);

		return productService.registProduct(sellerCode, request);
	}

	private void increasePopularCount(String productCode, int count) {
		for (int i = 0; i < count; i++) {
			productViewAdapter.increasePopularCount(productCode);
		}
	}

	private Document createDocument(String productCode, String title, Long price) {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("productCode", productCode);
		metadata.put("title", title);
		metadata.put("price", price);
		metadata.put("deleteStatus", DeleteStatus.N.name());

		return new Document(productCode, title + " " + price, metadata);
	}
}
