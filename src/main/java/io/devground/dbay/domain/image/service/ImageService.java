package io.devground.dbay.domain.image.service;

import java.util.List;

import io.devground.core.model.vo.ImageType;

public interface ImageService {

	void saveImages(ImageType imageType, String referenceCode, List<String> imageUrls);

	void saveImage(ImageType imageType, String referenceCode, String url);

	String getImageByCode(ImageType imageType, String referenceCode);

	void deleteImageByReferences(ImageType imageType, String referenceCode);

	void deleteImagesByReferencesAndUrl(ImageType imageType, String referenceCode, String url);
}
