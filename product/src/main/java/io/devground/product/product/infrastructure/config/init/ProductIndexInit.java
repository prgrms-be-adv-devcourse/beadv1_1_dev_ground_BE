package io.devground.product.product.infrastructure.config.init;

import java.time.Duration;
import java.util.Collections;
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

import io.devground.product.product.domain.model.Product;
import io.devground.product.product.infrastructure.adapter.out.ProductJpaRepository;
import io.devground.product.product.infrastructure.mapper.ProductMapper;
import io.devground.product.product.infrastructure.model.persistence.ProductDocument;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.util.ProductESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// TODO: 추후 Spring Batch 사용하여 작업 변경. 현재는 비동기로 Bulk 처리
@Component
@Slf4j(topic = "esBatch")
@Profile({"stage", "deploy"})
@RequiredArgsConstructor
public class ProductIndexInit implements ApplicationRunner {

	private final ProductJpaRepository productRepository;
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
			log.error("인덱스 초기화 실행 중 예외 발생 - [ErrorMsg={}]", e.getMessage());
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

		return getAllProductDocumentsFlux(totalFailed)
			.buffer(BATCH_SIZE)
			.doOnNext(batch -> {
				long curBatch = batchCount.incrementAndGet();

				log.info("[Batch={}] [BatchSize={}] Product 배치 인덱싱 시작", curBatch, batch.size());
			})
			.flatMap(batch ->
					reactiveElasticsearchOperations.saveAll(batch, ProductDocument.class)
						.collectList()
						.doOnSuccess(results -> {
							long indexed = results.size();
							totalIndexed.addAndGet(indexed);
						})
						.onErrorResume(e -> {
							totalFailed.addAndGet(batch.size());

							log.error("[Batch={}] [FailSize={}] 해당 배치 인덱싱 실패 - [ErrorMsg={}]",
								batchCount.get(), batch.size(), e.getMessage());

							return Mono.just(Collections.emptyList());
						})
				, CONCURRENCY)
			.then()
			.doOnSuccess(unused -> {
				long duration = System.currentTimeMillis() - startTime;
				log.info("[IndexedCount={}], [FailCount={}], [Time Duration={}s] Product 인덱싱 완료",
					totalIndexed.get(), totalFailed.get(), duration / 1000.0);
			})
			.timeout(Duration.ofMinutes(10))
			.onErrorResume(Throwable.class, e -> {
				log.error("인덱스 배치 처리 타임아웃/에러 [ErrorMsg={}]", e.getMessage());

				return Mono.empty();
			});
	}

	private Flux<ProductDocument> getAllProductDocumentsFlux(AtomicLong totalFailed) {

		return Flux.<ProductDocument>create(sink -> {
				int pageNumber = 0;

				while (true) {
					try {
						Page<ProductEntity> page = productRepository.findAllWithCategories(
							PageRequest.of(pageNumber, PAGE_SIZE));

						if (page.isEmpty()) {
							sink.complete();
							break;
						}

						for (ProductEntity productEntity : page.getContent()) {
							Product product = ProductMapper.toProductDomain(productEntity, productEntity.getProductSale());

							try {
								ProductDocument document = ProductESUtil.toProductDocument(product);

								if (document == null) {
									totalFailed.incrementAndGet();
									continue;
								}

								sink.next(document);
							} catch (Exception e) {
								log.error("[ProductCode={}] Product -> Document 변환 실패 - [ErrorMsg={}]",
									product.getCode(), e.getMessage());
							}
						}

						log.info("[Page={}] [Products={}] Product 페이지 호출 및 FLUX 변환 성공",
							pageNumber + 1, page.getNumberOfElements());

						pageNumber++;
					} catch (Exception e) {
						log.error("[Page={}] Product 페이지 호출 및 FLUX 변환 실패 - [ErrorMsg={}]", pageNumber, e.getMessage());
						sink.error(e);
						break;
					}
				}
			})
			.subscribeOn(Schedulers.boundedElastic());
	}

	// TODO: 비동기 상품 재인덱싱 필요 시 추가
}
