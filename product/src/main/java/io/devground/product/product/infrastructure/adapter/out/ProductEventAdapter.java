package io.devground.product.product.infrastructure.adapter.out;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.devground.product.product.application.port.out.ProductEventPort;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.infrastructure.model.vo.ProductCreateEvent;
import io.devground.product.product.infrastructure.model.vo.ProductDeleteEvent;
import io.devground.product.product.infrastructure.model.vo.ProductUpdateEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventAdapter implements ProductEventPort {

	private final ApplicationEventPublisher publisher;

	@Override
	public void publishCreated(Product product) {

		publisher.publishEvent(new ProductCreateEvent(product));
	}

	@Override
	public void publishUpdated(Product product) {

		publisher.publishEvent(new ProductUpdateEvent(product));
	}

	@Override
	public void publishDeleted(Product product) {

		publisher.publishEvent(new ProductDeleteEvent(product));
	}
}
