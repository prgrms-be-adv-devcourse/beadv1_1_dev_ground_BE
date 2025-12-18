package io.devground.product.product.application.port.out;

import io.devground.product.product.domain.model.Product;

public interface ProductEventPort {

	void publishCreated(Product product);

	void publishUpdated(Product product);

	void publishDeleted(Product product);
}
