package io.devground.product.product.infrastructure.adapter.out;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.devground.core.model.exception.ServiceException;
import io.devground.product.product.application.port.out.ProductVectorPort;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.infrastructure.mapper.ProductMapper;
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

	private void upsert(Product product) {
		String productCode = product.getCode();

		MDC.put("productId", String.valueOf(product.getId()));
		MDC.put("productCode", productCode);

		try {
			Document document = ProductMapper.toVectorDocument(product);

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
