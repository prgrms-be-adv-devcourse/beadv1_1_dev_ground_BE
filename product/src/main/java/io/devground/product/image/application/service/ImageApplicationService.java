package io.devground.product.image.application.service;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.devground.core.model.vo.ImageType;
import io.devground.product.image.application.persistence.ImagePersistencePort;
import io.devground.product.image.application.util.ImageUtil;
import io.devground.product.image.domain.model.Image;
import io.devground.product.image.domain.port.in.ImageUseCase;
import io.devground.product.image.domain.vo.ImageDomainErrorCode;
import io.devground.product.image.domain.vo.ImageSpec;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageApplicationService implements ImageUseCase {

	private final ImagePersistencePort imagePort;

	@Override
	public List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions) {

		ImageUtil.validateImageExtensions(fileExtensions);

		return imagePort.generatePresignedUrls(imageType, referenceCode, fileExtensions);
	}

	@Override
	public String saveImages(ImageType imageType, String referenceCode, List<String> imageUrls) {

		if (imageUrls == null || imageUrls.isEmpty()) {
			return null;
		}

		ImageUtil.validateImageUrls(imageUrls);

		// 1. 이미지 조회
		List<Image> foundImages = imagePort.getImages(imageType, referenceCode);

		List<String> foundUrls = foundImages.stream()
			.map(image -> image.getImageSpec().imageUrl())
			.toList();

		List<String> newUrls = imageUrls.stream()
			.distinct()
			.filter(url -> !foundUrls.contains(url))
			.toList();

		List<Image> newImages = newUrls.stream()
			.map(url -> new Image(new ImageSpec(referenceCode, imageType, url)))
			.toList();

		// 2. 중복 제거 후 이미지 저장
		if (!newImages.isEmpty()) {
			imagePort.saveImages(newImages);
		}

		// 3. 썸네일 추출
		return foundImages.isEmpty()
			? newImages.getFirst().getImageSpec().imageUrl()
			: foundImages.getFirst().getImageSpec().imageUrl();
	}

	@Override
	public List<URL> updateUrls(
		ImageType imageType, String referenceCode, List<String> deleteUrls, List<String> newImageExtensions
	) {

		if (!StringUtils.hasText(referenceCode)) {
			ImageDomainErrorCode.REFERENCE_CODE_MUST_BE_INPUT.throwException();
		}

		ImageUtil.validateImageUrls(deleteUrls);
		ImageUtil.validateImageExtensions(newImageExtensions);

		// 1. 삭제할 이미지가 존재하면 삭제
		if (deleteUrls != null && !deleteUrls.isEmpty()) {
			List<Image> imagesToDelete = imagePort.getTargetImages(imageType, referenceCode, deleteUrls);

			if (!imagesToDelete.isEmpty()) {
				imagePort.deleteAllImages(imagesToDelete);
			}
		}

		// 2. 새로운 PresignedUrl 발급
		if (CollectionUtils.isEmpty(newImageExtensions)) {
			return List.of();
		}

		return this.generatePresignedUrls(imageType, referenceCode, newImageExtensions);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getImageUrls(ImageType imageType, String referenceCode) {

		List<Image> images = imagePort.getImages(imageType, referenceCode);

		return images.stream().map(image -> image.getImageSpec().imageUrl()).toList();
	}

	@Override
	public void deleteImageByReferences(ImageType imageType, String referenceCode) {

		List<Image> images = imagePort.getImages(imageType, referenceCode);

		if (images.isEmpty()) {
			return;
		}

		imagePort.deleteAllImages(images);
	}

	@Override
	public void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls) {

		ImageUtil.validateImageUrls(urls);

		List<Image> images = imagePort.getTargetImages(imageType, referenceCode, urls);

		if (images.isEmpty()) {
			return;
		}

		imagePort.deleteAllImages(images);
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
