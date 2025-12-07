package io.devground.image.application.persistence;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;

public interface ImagePersistencePort {

	List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions);

	List<String> getImageUrls(ImageType imageType, String referenceCode);

	void saveImages(ImageType imageType, String referenceCode, List<String> newUrls);

	void deleteImages(ImageType imageType, String referenceCode, List<String> deleteUrls);
}
