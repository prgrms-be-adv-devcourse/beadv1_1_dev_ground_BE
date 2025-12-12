package io.devground.product.product.infrastructure.adapter.out;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.devground.product.product.application.port.out.ProductViewPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductViewAdapter implements ProductViewPort {

	private static final String VIEW_KEY_PREFIX = "user:view:";
	private static final String POPULAR_KEY_PREFIX = "product:popular:view:";
	private static final int MAX_SIZE = 30;
	private static final int TTL_DAYS = 7;

	private final StringRedisTemplate redisTemplate;

	@Override
	public void saveView(String userCode, String productCode) {

		if (!StringUtils.hasText(userCode) || !StringUtils.hasText(productCode)) {
			return;
		}

		String key = this.generateViewKey(userCode);
		ListOperations<String, String> productRedisList = redisTemplate.opsForList();

		try {
			productRedisList.remove(key, 0, productCode);
			productRedisList.leftPush(key, productCode);
			productRedisList.trim(key, 0, MAX_SIZE - 1);

			redisTemplate.expire(key, Duration.ofDays(TTL_DAYS));
		} catch (Exception e) {
			log.error("[ProductCode:{}] 사용자 조회 상품 Redis 저장 실패", productCode, e);
		}
	}

	@Override
	public List<String> getLatestViewedProductCodes(String userCode, int size) {

		if (!StringUtils.hasText(userCode) || size <= 0) {
			return List.of();
		}

		String key = this.generateViewKey(userCode);
		List<String> productCodes = redisTemplate.opsForList()
			.range(key, 0, size - 1);

		return productCodes != null ? productCodes : List.of();
	}

	@Override
	public void increasePopularCount(String productCode) {

		if (!StringUtils.hasText(productCode)) {
			return;
		}

		String key = this.generatePopularKey(LocalDate.now());

		try {
			redisTemplate.opsForZSet()
				.incrementScore(key, productCode, 1.0);

			redisTemplate.expire(key, Duration.ofDays(TTL_DAYS));
		} catch (Exception e) {
			log.error("[ProductCode:{}] 인기 상품 카운트 증가 실패", productCode, e);
		}
	}

	@Override
	public List<String> getTopProductCodes(int size) {

		if (size <= 0) {
			return List.of();
		}

		Map<String, Double> scoreMap = new HashMap<>();
		LocalDate now = LocalDate.now();

		for (int i = 0; i < TTL_DAYS; i++) {
			LocalDate before = now.minusDays(i);
			String key = this.generatePopularKey(before);

			Set<ZSetOperations.TypedTuple<String>> zSetTuples = redisTemplate.opsForZSet()
				.reverseRangeWithScores(key, 0, size - 1);

			if (CollectionUtils.isEmpty(zSetTuples)) {
				continue;
			}

			for (ZSetOperations.TypedTuple<String> zSetTuple : zSetTuples) {
				String productCode = zSetTuple.getValue();
				Double score = zSetTuple.getScore();

				if (productCode != null && score != null) {
					scoreMap.merge(productCode, score, Double::sum);
				}
			}
		}

		return scoreMap.entrySet().stream()
			.sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
			.limit(size)
			.map(Map.Entry::getKey)
			.toList();
	}

	private String generateViewKey(String userCode) {

		return VIEW_KEY_PREFIX + userCode;
	}

	private String generatePopularKey(LocalDate date) {

		return POPULAR_KEY_PREFIX + date;
	}
}
