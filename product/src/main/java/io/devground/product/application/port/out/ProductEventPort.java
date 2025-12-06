package io.devground.product.application.port.out;

import io.devground.product.domain.model.Product;

public interface ProductEventPort {

	void publishCreated(Product product);

	void publishUpdated(Product product);

	void publishDeleted(Product product);
}
