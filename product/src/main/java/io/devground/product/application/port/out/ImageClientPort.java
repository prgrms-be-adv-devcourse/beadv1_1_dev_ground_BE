package io.devground.product.application.port.out;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;
import io.devground.product.application.model.vo.ApplicationImageType;
import io.devground.product.infrastructure.model.web.request.ImageUpdatePlan;

public interface ImageClientPort {

	List<String> getImageUrls(String productCode, ApplicationImageType imageType);

	List<URL> prepareUploadUrls(ImageType imageType, String productCode, List<String> fileExtensions);

	List<URL> updateImages(ImageUpdatePlan request);
}
