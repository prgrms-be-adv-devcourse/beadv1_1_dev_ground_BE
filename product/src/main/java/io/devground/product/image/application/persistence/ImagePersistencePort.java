package io.devground.product.image.application.persistence;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;
import io.devground.product.image.domain.model.Image;

public interface ImagePersistencePort {

	List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions);

	List<Image> getImages(ImageType imageType, String referenceCode);

	List<Image> getTargetImages(ImageType imageType, String referenceCode, List<String> urls);

	void saveImages(List<Image> images);

	void deleteAllImages(List<Image> images);

	String compensateToS3Upload(ImageType imageType, String referenceCode, List<String> urls);

	String compensateUpload(ImageType imageType, String referenceCode, List<String> urls);
}
