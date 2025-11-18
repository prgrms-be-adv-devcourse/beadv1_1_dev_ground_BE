package io.devground.dbay.domain.product.product.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.devground.dbay.domain.product.product.model.entity.Product;
import io.devground.dbay.domain.product.product.model.entity.ProductDocument;
import io.devground.dbay.domain.product.product.repository.ProductSearchRepository;
import io.devground.dbay.domain.product.product.util.elasticsearch.ProductESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "esIndex")
@RequiredArgsConstructor
public class ProductIndexService {

	private final ProductSearchRepository productSearchRepository;

	@Async("esTaskExecutor")
	public CompletableFuture<Void> indexProduct(Product product) {

		String productCode = product.getCode();

		MDC.put("productCode", productCode);
		MDC.put("productId", String.valueOf(product.getId()));

		try {
			ProductDocument productDocument = ProductESUtil.toProductDocument(product);
			productSearchRepository.save(productDocument);

			log.info("Product 인덱싱 성공 - ProductCode: {}, ProductId: {}", productCode, product.getId());

			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("Product 인덱싱 실패 - ProductCode: {}, ProductId: {}, Exception: {}",
				productCode, product.getId(), e.getMessage());

			return CompletableFuture.failedFuture(e);
		} finally {
			MDC.clear();
		}
	}

	@Async("esTaskExecutor")
	public CompletableFuture<Void> updateProduct(Product product) {
		return this.indexProduct(product);
	}

	@Async("esTaskExecutor")
	public CompletableFuture<Void> deleteProduct(Product product) {

		String productCode = product.getCode();
		MDC.put("productCode", productCode);

		try {
			indexProduct(product);

			log.info("Product 인덱스 소프트 딜리트 성공 - ProductCode: {}, ProductId: {}", productCode, product.getId());

			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("Product 인덱스 소프트 딜리트 실패 - ProductCode: {}, ProductId: {}, Exception: {}",
				productCode, product.getId(), e.getMessage());

			return CompletableFuture.failedFuture(e);
		} finally {
			MDC.clear();
		}
	}

	@Async("esTaskExecutor")
	public CompletableFuture<Void> completelyDeleteProduct(Long productId) {

		MDC.put("productId", String.valueOf(productId));

		try {
			productSearchRepository.deleteById(productId);

			log.info("Product 인덱스 완전 삭제 성공 - ProductId: {}", productId);

			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			log.error("Product 인덱스 완전 삭제 실패 - ProductId: {}, Exception: {}", productId, e.getMessage());

			return CompletableFuture.failedFuture(e);
		} finally {
			MDC.clear();
		}
	}
}
