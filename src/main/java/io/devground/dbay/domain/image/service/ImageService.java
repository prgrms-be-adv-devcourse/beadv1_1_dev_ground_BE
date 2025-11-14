package io.devground.dbay.domain.image.service;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;

public interface ImageService {

	// TODO: 사용하지 않을 시 삭제
	// Void saveImages(UploadImagesRequest request, MultipartFile[] files);

	List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions);

	void saveImages(ImageType imageType, String referenceCode, List<String> imageUrls);

	List<URL> updateUrls(
		ImageType imageType, String referenceCode, List<String> deleteUrls, List<String> newImageExtensions
	);

	String getImageByCode(ImageType imageType, String referenceCode);

	void deleteImageByReferences(ImageType imageType, String referenceCode);

	void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls);

	Void compensateUpload(ImageType imageType, String referenceCode, List<String> imageUrls);
}
