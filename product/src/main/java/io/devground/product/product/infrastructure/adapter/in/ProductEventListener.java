package io.devground.product.product.infrastructure.adapter.in;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.devground.product.product.application.port.out.persistence.ProductSearchPort;
import io.devground.product.product.infrastructure.model.vo.ProductCreateEvent;
import io.devground.product.product.infrastructure.model.vo.ProductDeleteEvent;
import io.devground.product.product.infrastructure.model.vo.ProductUpdateEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventListener {

	private final ProductSearchPort searchPort;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductDocumentIndex(ProductCreateEvent event) {

		searchPort.prepareSearch(event.product());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductUpdateIndex(ProductUpdateEvent event) {

		searchPort.updateSearch(event.product());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductDocumentDelete(ProductDeleteEvent event) {

		searchPort.deleteSearch(event.product());
	}
}
