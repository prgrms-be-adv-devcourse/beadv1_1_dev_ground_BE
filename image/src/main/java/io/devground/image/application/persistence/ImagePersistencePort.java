package io.devground.image.application.persistence;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;
import io.devground.image.domain.model.Image;

public interface ImagePersistencePort {

	List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions);

	List<Image> getImages(ImageType imageType, String referenceCode);

	void saveImages(List<Image> images);

	void deleteImages(ImageType imageType, String referenceCode, List<String> deleteUrls);

	void deleteAllImages(ImageType imageType, String referenceCode);

	void deleteTargetImages(ImageType imageType, String referenceCode, List<String> urls);

	String compensateToS3Upload(ImageType imageType, String referenceCode, List<String> urls);

	String compensateUpload(ImageType imageType, String referenceCode, List<String> urls);
}
