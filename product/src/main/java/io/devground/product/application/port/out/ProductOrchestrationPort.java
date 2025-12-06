package io.devground.product.application.port.out;

import java.net.URL;
import java.util.List;

import io.devground.core.event.image.ImageProcessedEvent;

public interface ProductOrchestrationPort {

	void uploadProductImages(String sellerCode, String productSellerCode, String productCode, List<String> urls);

	void handleImageProcessSuccess(String sagaId, ImageProcessedEvent event);

	void handleImageProcessFailure(String sagaId, ImageProcessedEvent event);

	List<URL> updateProductImages(String productCode, List<String> deleteUrls, List<String> newExtensions);

	void deleteProductImages(String productCode);
}
