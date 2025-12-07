package io.devground.product.application.port.out;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;
import io.devground.product.application.model.vo.ApplicationImageType;

public interface ImageClientPort {

	List<String> getImageUrls(String productCode, ApplicationImageType imageType);

	List<URL> prepareUploadUrls(ImageType imageType, String productCode, List<String> fileExtensions);

	List<URL> updateImages(
		ImageType imageType, String productCode, List<String> deleteUrls, List<String> newImageExtensions
	);
}
