package io.devground.dbay.domain.image.service;

import java.util.List;

import io.devground.core.model.vo.ImageType;

public interface ImageService {

	// TODO: 사용하지 않을 시 삭제
	// Void saveImages(UploadImagesRequest request, MultipartFile[] files);

	void saveImages(ImageType imageType, String referenceCode, List<String> imageUrls);

	void saveImage(ImageType imageType, String referenceCode, String url);

	String getImageByCode(ImageType imageType, String referenceCode);

	void deleteImageByReferences(ImageType imageType, String referenceCode);

	Void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls);
}
