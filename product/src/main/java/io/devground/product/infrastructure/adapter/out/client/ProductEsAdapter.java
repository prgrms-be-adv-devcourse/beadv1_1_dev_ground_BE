package io.devground.product.infrastructure.adapter.out.client;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.devground.core.model.exception.ServiceException;
import io.devground.product.application.port.out.persistence.ProductSearchPort;
import io.devground.product.domain.exception.DomainException;
import io.devground.product.domain.model.Product;
import io.devground.product.infrastructure.adapter.out.ProductSearchRepository;
import io.devground.product.infrastructure.model.persistence.ProductDocument;
import io.devground.product.infrastructure.util.ProductESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "esIndex")
@RequiredArgsConstructor
public class ProductEsAdapter implements ProductSearchPort {

	private final ProductSearchRepository productSearchRepository;

	@Override
	@Async("esTaskExecutor")
	public void prepareSearch(Product product) {

		String productCode = product.getCode();

		MDC.put("productId", String.valueOf(product.getId()));
		MDC.put("productCode", productCode);

		try {
			ProductDocument productDocument = ProductESUtil.toProductDocument(product);
			productSearchRepository.save(productDocument);
		} catch (DomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 인덱싱 실패");

			throw e;
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 인덱싱 실패 - ExStack: ", e);

			throw e;
		} finally {
			MDC.clear();
		}
	}
}
