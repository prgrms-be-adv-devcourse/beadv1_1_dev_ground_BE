package io.devground.product.product.infrastructure.adapter.in;

import java.util.concurrent.RejectedExecutionException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.devground.product.product.application.port.out.ProductVectorPort;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.infrastructure.model.vo.ProductCreateEvent;
import io.devground.product.product.infrastructure.model.vo.ProductDeleteEvent;
import io.devground.product.product.infrastructure.model.vo.ProductUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "vectorIndex")
@Component
@RequiredArgsConstructor
public class ProductVectorIndexEventListener {

	private final ProductVectorPort vectorPort;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductDocumentIndex(ProductCreateEvent event) {

		Product product = event.product();

		try {
			vectorPort.prepareVector(event.product());
		} catch (RejectedExecutionException e) {
			MDC.put("productId", String.valueOf(product.getId()));
			MDC.put("productCode", product.getCode());
			MDC.put("errorMsg", e.getMessage());

			log.error("벡터 인덱싱 등록 요청 초과로 인한 누락");
		} finally {
			MDC.clear();
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductUpdateIndex(ProductUpdateEvent event) {

		Product product = event.product();

		try {
			vectorPort.updateVector(event.product());
		} catch (RejectedExecutionException e) {
			MDC.put("productId", String.valueOf(product.getId()));
			MDC.put("productCode", product.getCode());
			MDC.put("errorMsg", e.getMessage());

			log.error("벡터 인덱싱 수정 요청 초과로 인한 누락");
		} finally {
			MDC.clear();
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductDocumentDelete(ProductDeleteEvent event) {

		Product product = event.product();

		try {
			vectorPort.deleteVector(event.product());
		} catch (RejectedExecutionException e) {
			MDC.put("productId", String.valueOf(product.getId()));
			MDC.put("productCode", product.getCode());
			MDC.put("errorMsg", e.getMessage());

			log.error("벡터 인덱싱 삭제 요청 초과로 인한 누락");
		} finally {
			MDC.clear();
		}
	}
}

