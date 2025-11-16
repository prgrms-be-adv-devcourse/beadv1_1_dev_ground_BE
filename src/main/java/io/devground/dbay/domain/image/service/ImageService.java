package io.devground.dbay.domain.image.service;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;

public interface ImageService {

	List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions);

	String saveImages(ImageType imageType, String referenceCode, List<String> imageUrls);

	List<URL> updateUrls(
		ImageType imageType, String referenceCode, List<String> deleteUrls, List<String> newImageExtensions
	);

	List<String> getImagesByCode(ImageType imageType, String referenceCode);

	void deleteImageByReferences(ImageType imageType, String referenceCode);

	void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls);

	Void compensateUpload(ImageType imageType, String referenceCode);
}
