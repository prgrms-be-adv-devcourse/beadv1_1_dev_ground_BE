package io.devground.product.product.infrastructure.adapter.out;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.devground.core.model.exception.ServiceException;
import io.devground.product.product.application.port.out.persistence.ProductPrepareSearchPort;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductSearchRepository;
import io.devground.product.product.infrastructure.model.persistence.ProductDocument;
import io.devground.product.product.infrastructure.util.ProductESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "esIndex")
@RequiredArgsConstructor
public class ProductEsAdapter implements ProductPrepareSearchPort {

	private final ProductSearchRepository productSearchRepository;

	@Override
	@Async("esTaskExecutor")
	public void prepareSearch(Product product) {

		this.indexProduct(product);
	}

	@Override
	@Async("esTaskExecutor")
	public void updateSearch(Product product) {

		this.indexProduct(product);
	}

	@Override
	@Async("esTaskExecutor")
	public void deleteSearch(Product product) {

		this.indexProduct(product);
	}

	private void indexProduct(Product product) {

		String productCode = product.getCode();

		MDC.put("productId", String.valueOf(product.getId()));
		MDC.put("productCode", productCode);

		try {
			ProductDocument productDocument = ProductESUtil.toProductDocument(product);
			productSearchRepository.save(productDocument);
		} catch (ProductDomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 인덱싱 실패");
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("Product 인덱싱 실패 - ExStack: ", e);
		} finally {
			MDC.clear();
		}
	}
}
