package io.devground.image.application.service;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ImageType;
import io.devground.image.application.persistence.ImagePersistencePort;
import io.devground.image.domain.port.in.ImageUseCase;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageApplicationService implements ImageUseCase {

	private final ImagePersistencePort imagePort;

	@Override
	public List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions) {

		return imagePort.generatePresignedUrls(imageType, referenceCode, fileExtensions);
	}

	@Override
	public String saveImages(ImageType imageType, String referenceCode, List<String> imageUrls) {

		if (imageUrls == null || imageUrls.isEmpty()) {
			return null;
		}

		// 1. 이미지 조회
		List<String> foundUrls = imagePort.getImageUrls(imageType, referenceCode);

		List<String> newUrls = imageUrls.stream()
			.distinct()
			.filter(url -> !foundUrls.contains(url))
			.toList();

		// 2. 중복 제거 후 이미지 저장
		if (!newUrls.isEmpty()) {
			imagePort.saveImages(imageType, referenceCode, newUrls);
		}

		// 3. 썸네일 추출
		return foundUrls.isEmpty()
			? newUrls.getFirst()
			: foundUrls.getFirst();
	}

	@Override
	public List<URL> updateUrls(
		ImageType imageType, String referenceCode, List<String> deleteUrls, List<String> newImageExtensions
	) {

		// 1. 삭제할 이미지가 존재하면 삭제
		if (deleteUrls != null && deleteUrls.isEmpty()) {
			imagePort.deleteImages(imageType, referenceCode, deleteUrls);
		}

		// 2. 새로운 PresignedUrl 발급
		return this.generatePresignedUrls(imageType, referenceCode, newImageExtensions);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getImagesByCode(ImageType imageType, String referenceCode) {

		return imagePort.getImageUrls(imageType, referenceCode);
	}

	@Override
	public void deleteImageByReferences(ImageType imageType, String referenceCode) {

		imagePort.deleteAllImages(imageType, referenceCode);
	}

	@Override
	public void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls) {

		if (urls == null || urls.isEmpty()) {
			return;
		}

		imagePort.deleteTargetImages(imageType, referenceCode, urls);
	}

	@Override
	public String compensateToS3Upload(ImageType imageType, String referenceCode, List<String> urls) {

		// DB에 처음으로 저장된 이미지를 썸네일로 대체 사용
		return imagePort.compensateToS3Upload(imageType, referenceCode, urls);
	}

	@Override
	public String compensateUpload(ImageType imageType, String referenceCode, List<String> urls) {

		// DB에 처음으로 저장된 이미지를 썸네일로 대체 사용
		return imagePort.compensateUpload(imageType, referenceCode, urls);
	}
}
