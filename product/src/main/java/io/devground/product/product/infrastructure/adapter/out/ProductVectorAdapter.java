package io.devground.product.product.infrastructure.adapter.out;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.DeleteStatus;
import io.devground.product.product.application.port.out.ProductVectorPort;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.vo.ProductRecommendSpec;
import io.devground.product.product.infrastructure.util.ProductVectorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "vectorIndex")
@Component
@RequiredArgsConstructor
public class ProductVectorAdapter implements ProductVectorPort {

	private final VectorStore vectorStore;

	@Override
	@Async("vectorTaskExecutor")
	public void prepareVector(Product product) {
		upsert(product);
	}

	@Override
	@Async("vectorTaskExecutor")
	public void updateVector(Product product) {
		upsert(product);
	}

	@Override
	@Async("vectorTaskExecutor")
	public void deleteVector(Product product) {
		String productCode = product.getCode();

		MDC.put("productId", String.valueOf(product.getId()));
		MDC.put("productCode", productCode);

		try {
			vectorStore.delete(List.of(product.getCode()));
		} catch (ProductDomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 벡터 삭제 인덱싱 실패");
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 벡터 삭제 인덱싱 실패 - ExStack: ", e);
		} finally {
			MDC.clear();
		}
	}

	@Override
	public List<ProductRecommendSpec> recommendByUserView(List<Product> products, int size) {

		if (CollectionUtils.isEmpty(products)) {
			return List.of();
		}

		String query = ProductVectorUtil.createQueryFromUserViewedProducts(products);

		if (!StringUtils.hasText(query)) {
			return List.of();
		}

		Set<String> productCodeSet = products.stream()
			.map(Product::getCode)
			.collect(Collectors.toSet());

		try {
			List<Document> documents = vectorStore.similaritySearch(
				SearchRequest.builder()
					.query(query)
					.topK(size * 2 + productCodeSet.size())
					.build()
			);

			return documents.stream()
				.filter(document -> {
					String productCode = (String) document.getMetadata().getOrDefault("productCode", document.getId());
					return !productCodeSet.contains(productCode);
				})
				.filter(document -> {
					String deleteStatus =
						document.getMetadata().getOrDefault("deleteStatus", DeleteStatus.N).toString();
					return DeleteStatus.N.name().equalsIgnoreCase(deleteStatus);
				})
				.limit(size)
				.map(ProductVectorUtil::toRecommendSpec)
				.toList();
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("사용자 조회 기반 벡터 추천 실패 - ExStack: ", e);
			MDC.clear();

			return List.of();
		}
	}

	@Override
	public List<ProductRecommendSpec> recommendByProductDetail(Product product, int size) {

		String query = ProductVectorUtil.createQueryFromProductDetail(product);

		try {
			List<Document> documents = vectorStore.similaritySearch(
				SearchRequest.builder()
					.query(query)
					.topK(size * 2)
					.build()
			);

			String selfCode = product.getCode();

			return documents.stream()
				.filter(document -> {
					String productCode = (String) document.getMetadata().getOrDefault("productCode", document.getId());
					return !productCode.equals(selfCode);
				})
				.filter(document -> {
					String deleteStatus =
						document.getMetadata().getOrDefault("deleteStatus", DeleteStatus.N).toString();
					return DeleteStatus.N.name().equalsIgnoreCase(deleteStatus);
				})
				.limit(size)
				.map(ProductVectorUtil::toRecommendSpec)
				.toList();
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("상품 상세 기반 벡터 추천 실패 - ExStack: ", e);
			MDC.clear();

			return List.of();
		}
	}

	private void upsert(Product product) {
		String productCode = product.getCode();

		MDC.put("productId", String.valueOf(product.getId()));
		MDC.put("productCode", productCode);

		try {
			Document document = ProductVectorUtil.toVectorDocument(product);

			vectorStore.add(List.of(document));
		} catch (ProductDomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 벡터 업서트 인덱싱 실패");
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 벡터 업서트 인덱싱 실패 - ExStack: ", e);
		} finally {
			MDC.clear();
		}
	}
}
