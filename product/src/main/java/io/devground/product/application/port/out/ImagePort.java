package io.devground.product.application.port.out;

import java.net.URL;
import java.util.List;

import io.devground.product.application.model.vo.ApplicationImageType;
import io.devground.product.infrastructure.model.web.request.GeneratePresignedRequest;

public interface ImagePort {

	List<String> getImageUrls(String productCode, ApplicationImageType imageType);

	List<URL> generatePresignedUrls(GeneratePresignedRequest request);
}
