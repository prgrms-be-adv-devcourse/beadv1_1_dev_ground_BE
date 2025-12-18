package io.devground.product.product.application.port.out;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.product.infrastructure.adapter.out.ProductViewAdapter;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProductViewPortTest {

	@Autowired
	ProductViewAdapter productViewAdapter;

	@Autowired
	StringRedisTemplate redisTemplate;

	private static final String USER_CODE = "testUser";
	private static final String PRODUCT_CODE = "testProduct";
	private static final String VIEW_KEY_PREFIX = "user:view:";
	private static final String POPULAR_KEY_PREFIX = "product:popular:view:";
	private static final int MAX_SIZE = 30;

	@BeforeEach
	void setUp() {
		redisTemplate.execute((RedisConnection conn) -> {
			conn.serverCommands().flushAll();
			return null;
		});
	}

	@AfterEach
	void setDown() {
		redisTemplate.execute((RedisConnection conn) -> {
			conn.serverCommands().flushAll();
			return null;
		});
	}

	@Test
	@DisplayName("성공_사용자 조회 이력 저장")
	void success_save_view() {

		// given, when
		productViewAdapter.saveView(USER_CODE, PRODUCT_CODE);

		// then
		List<String> productCodes = redisTemplate.opsForList()
			.range(VIEW_KEY_PREFIX + USER_CODE, 0, -1);

		assertEquals(1, productCodes.size());
		assertEquals(PRODUCT_CODE, productCodes.getFirst());
	}

	@Test
	@DisplayName("성공_사용자 조회 이력 저장 - 중복 제거 확인")
	void success_save_view_remove_duplicate() throws Exception {

		// given
		String userCode = USER_CODE;
		String productCode = PRODUCT_CODE;

		// when
		productViewAdapter.saveView(userCode, productCode);
		productViewAdapter.saveView(userCode, productCode);

		// then
		List<String> productCodes = redisTemplate.opsForList()
			.range(VIEW_KEY_PREFIX + userCode, 0, -1);

		assertEquals(1, productCodes.size());
		assertEquals(productCode, productCodes.getFirst());
	}

	@Test
	@DisplayName("성공_사용자 조회 이력 저장 - 최대 개수 유지 및 최신순 N개 조회 확인")
	void success_save_view_in_max_size() throws Exception {

		// given
		String userCode = USER_CODE;

		// when
		for (int i = 1; i <= MAX_SIZE + 10; i++) {
			productViewAdapter.saveView(userCode, PRODUCT_CODE + i);
		}

		// then
		List<String> productCodes = redisTemplate.opsForList()
			.range(VIEW_KEY_PREFIX + userCode, 0, -1);

		assertEquals(MAX_SIZE, productCodes.size());
		assertEquals(PRODUCT_CODE + (MAX_SIZE + 10), productCodes.getFirst());
		assertEquals(PRODUCT_CODE + "11", productCodes.getLast());
	}

	@Test
	@DisplayName("성공_사용자 조회 이력 조회 - 빈 사용자 코드")
	void success_get_empty_user_code() throws Exception {

		// given, when
		List<String> productCodes = productViewAdapter.getLatestViewedProductCodes("", 10);

		// then
		assertThat(productCodes).isEmpty();
	}

	@Test
	@DisplayName("성공_인기 상품 카운트 증가")
	void success_increase_popular_count() throws Exception {

		// given
		String productCode = PRODUCT_CODE;
		LocalDate today = LocalDate.now();
		String key = POPULAR_KEY_PREFIX + today;

		// when
		productViewAdapter.increasePopularCount(productCode);
		productViewAdapter.increasePopularCount(productCode);
		productViewAdapter.increasePopularCount(productCode);
		productViewAdapter.increasePopularCount(productCode);
		productViewAdapter.increasePopularCount(productCode);

		// then
		Double score = redisTemplate.opsForZSet().score(key, productCode);
		assertEquals(5.0, score);
	}

	@Test
	@DisplayName("성공_인기 상품 상위 N개 조회")
	void success_get_popular_products() throws Exception {

		// given
		for (int i = 1; i <= 10; i++) {
			String productCode = PRODUCT_CODE + i;
			for (int j = 0; j < i; j++) {
				productViewAdapter.increasePopularCount(productCode);
			}
		}

		// when
		List<String> productCodes = productViewAdapter.getTopProductCodes(5);

		// then
		assertThat(productCodes).hasSize(5);
		assertEquals(PRODUCT_CODE + "10", productCodes.getFirst());
		assertEquals(PRODUCT_CODE + "9", productCodes.get(1));
		assertEquals(PRODUCT_CODE + "6", productCodes.getLast());
	}

	@Test
	@DisplayName("성공_인기 상품 7일 통합 집계")
	void success_popular_aggregation() throws Exception {

		// given
		LocalDate today = LocalDate.now();

		String todayKey = POPULAR_KEY_PREFIX + today;
		redisTemplate.opsForZSet().incrementScore(todayKey, PRODUCT_CODE + "1", 10.0);

		String yesterdayKey = POPULAR_KEY_PREFIX + today.minusDays(1);
		redisTemplate.opsForZSet().incrementScore(yesterdayKey, PRODUCT_CODE + "2", 5.0);

		String fiveDaysAgoKey = POPULAR_KEY_PREFIX + today.minusDays(5);
		redisTemplate.opsForZSet().incrementScore(fiveDaysAgoKey, PRODUCT_CODE + "5", 3.0);

		// when
		List<String> productCodes = productViewAdapter.getTopProductCodes(2);

		// then
		assertThat(productCodes).hasSize(2);
		assertEquals(PRODUCT_CODE + "1", productCodes.getFirst());
		assertEquals(PRODUCT_CODE + "2", productCodes.getLast());
	}

	@Test
	@DisplayName("성공_인기 상품 조회 - 빈 데이터")
	void success_get_empty_popular() throws Exception {

		// when
		List<String> productCodes = productViewAdapter.getTopProductCodes(10);

		// then
		assertThat(productCodes).isEmpty();
	}

	@Test
	@DisplayName("성공_인기 상품 조회 - size 보다 적은 데이터")
	void success_get_lease_popular() throws Exception {

		// given
		productViewAdapter.increasePopularCount(PRODUCT_CODE + "1");
		productViewAdapter.increasePopularCount(PRODUCT_CODE + "2");

		// when
		List<String> productCodes = productViewAdapter.getTopProductCodes(10);

		// then
		assertThat(productCodes).hasSize(2);
	}
}