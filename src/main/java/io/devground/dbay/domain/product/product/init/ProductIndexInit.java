package io.devground.dbay.domain.product.product.init;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Component;

import io.devground.dbay.domain.product.product.model.entity.Product;
import io.devground.dbay.domain.product.product.model.entity.ProductDocument;
import io.devground.dbay.domain.product.product.repository.ProductRepository;
import io.devground.dbay.domain.product.product.util.elasticsearch.ProductESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// TODO: 추후 Spring Batch 사용하여 작업 변경. 현재는 비동기로 Bulk 처리
@Component
@Slf4j(topic = "esIndex")
@Profile({"stage", "deploy"})
@RequiredArgsConstructor
public class ProductIndexInit implements ApplicationRunner {

	private final ProductRepository productRepository;
	private final ElasticsearchOperations elasticsearchOperations;
	private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

	private static final boolean ENABLED = true;
	private static final int BATCH_SIZE = 500;
	private static final int PAGE_SIZE = 500;
	private static final int CONCURRENCY = 3;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		if (!ENABLED) {
			return;
		}

		log.info("Product 벌크 인덱싱 비동기 시작");

		try {
			// 인덱스 생성
			autoCreateIndex();

			// 비동기로 Bulk 연산 수행
			bulkIndexAllProducts().subscribe();
		} catch (Exception e) {
			log.error("인덱스 초기화 실행 중 예외 발생, Exception: {}", e.getMessage());
		}
	}

	public void autoCreateIndex() {

		IndexOperations indexOperations = elasticsearchOperations.indexOps(ProductDocument.class);

		if (!indexOperations.exists()) {
			log.info("Product 인덱스 생성");

			indexOperations.create();
			indexOperations.putMapping();
		}
	}

	/**
	 * 사용 전략 -> Reactive + Bulk
	 * JPA 조회 -> boundedElastic 스레드풀을 통한 페이지네이션(cpu * 10)
	 * Product -> ProductDocument로의 컨버트를 병렬 처리
	 */
	private Mono<Void> bulkIndexAllProducts() {

		long startTime = System.currentTimeMillis();
		AtomicLong totalIndexed = new AtomicLong(0);
		AtomicLong totalFailed = new AtomicLong(0);
		AtomicLong batchCount = new AtomicLong(0);

		return getAllProductsFlux()
			.parallel()
			.runOn(Schedulers.parallel())
			.map(product -> {
				try {
					return ProductESUtil.toProductDocument(product);
				} catch (Exception e) {
					totalFailed.incrementAndGet();
					log.error("Product -> Document 변환 실패 - ProductCode: {}, Exception: {}",
						product.getCode(), e.getMessage());

					return null;
				}
			})
			.filter(Objects::nonNull)
			.sequential()
			.buffer(BATCH_SIZE)
			.doOnNext(batch -> {
				long curBatch = batchCount.incrementAndGet();

				log.info("Product 배치 인덱싱 시작 - Batch: {}, BatchSize: {}", curBatch, batch.size());
			})
			.flatMap(batch ->
					reactiveElasticsearchOperations.saveAll(batch, ProductDocument.class)
						.collectList()
						.doOnSuccess(results -> {
							long indexed = results.size();
							totalIndexed.addAndGet(indexed);

							log.info("성공 Batch: {}, Indexed: {}, SuccessSize: {}",
								batchCount.get(), indexed, totalIndexed.get());
						})
						.onErrorResume(e -> {
							totalFailed.addAndGet(batch.size());

							log.error("실패 Batch: {}, FailSize: {}, Exception: {}",
								batchCount.get(), batch.size(), e.getMessage());

							return Mono.just(Collections.emptyList());
						})
				, CONCURRENCY)
			.then()
			.doOnSuccess(unused -> {
				long duration = System.currentTimeMillis() - startTime;
				log.info("Product 인덱싱 완료 - IndexedCount: {}, FailCount: {}, Time Duration: {}s",
					totalIndexed.get(), totalFailed.get(), duration / 1000.0);
			})
			.timeout(Duration.ofMinutes(10))
			.onErrorResume(Throwable.class, e -> {
				log.error("인덱스 배치 처리 타임아웃/에러 - Exception: {}", e.getMessage());

				return Mono.empty();
			});
	}

	private Flux<Product> getAllProductsFlux() {

		return Flux.<Product>create(sink -> {
				int pageNumber = 0;

				while (true) {
					try {
						Page<Product> page = productRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));

						if (page.isEmpty()) {
							sink.complete();
							break;
						}

						page.getContent().forEach(sink::next);

						log.info("Product 페이지 호출 및 FLUX 변환 성공 - Page: {}, Products: {}",
							pageNumber + 1, page.getNumberOfElements());

						pageNumber++;
					} catch (Exception e) {
						log.error("Product 페이지 호출 및 FLUX 변환 실패 - Page: {}, Exception: {}", pageNumber, e.getMessage());
						sink.error(e);
						break;
					}
				}
			})
			.subscribeOn(Schedulers.boundedElastic());
	}

	// TODO: 비동기 상품 재인덱싱 필요 시 추가
}
