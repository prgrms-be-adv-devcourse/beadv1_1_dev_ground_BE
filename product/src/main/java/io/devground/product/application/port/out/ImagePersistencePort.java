package io.devground.product.application.port.out;

import java.net.URL;
import java.util.List;

import io.devground.product.application.model.vo.ApplicationImageType;
import io.devground.product.infrastructure.model.web.request.ImageUpdatePlan;
import io.devground.product.infrastructure.model.web.request.ImageUploadPlan;

public interface ImagePersistencePort {

	List<String> getImageUrls(String productCode, ApplicationImageType imageType);

	List<URL> prepareUploadUrls(ImageUploadPlan request);

	List<URL> updateImages(ImageUpdatePlan request);
}
