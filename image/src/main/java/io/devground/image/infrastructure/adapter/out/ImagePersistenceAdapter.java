package io.devground.image.infrastructure.adapter.out;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import io.devground.core.model.vo.ImageType;
import io.devground.image.application.persistence.ImagePersistencePort;
import io.devground.image.infrastructure.adapter.out.s3.S3Service;
import io.devground.image.infrastructure.mapper.ImageMapper;
import io.devground.image.infrastructure.model.persistence.ImageEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ImagePersistenceAdapter implements ImagePersistencePort {

	private final S3Service s3Service;
	private final ImageJpaRepository imageRepository;

	@Override
	public List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions) {

		return s3Service.generatePresignedUrls(imageType, referenceCode, fileExtensions);
	}

	@Override
	public List<String> getImageUrls(ImageType imageType, String referenceCode) {

		return imageRepository.findAllByImageTypeAndReferenceCode(imageType, referenceCode).stream()
			.map(ImageEntity::getImageUrl)
			.toList();
	}

	@Override
	public void saveImages(ImageType imageType, String referenceCode, List<String> newUrls) {

		List<ImageEntity> newImages = newUrls.stream()
			.map(url -> ImageMapper.of(imageType, referenceCode, url))
			.toList();

		imageRepository.saveAll(newImages);
	}

	@Override
	public void deleteImages(ImageType imageType, String referenceCode, List<String> deleteUrls) {

		List<ImageEntity> imagesToDelete =
			imageRepository.findAllByImageTypeAndReferenceCodeAndImageUrlIn(imageType, referenceCode, deleteUrls);

		if (!imagesToDelete.isEmpty()) {
			s3Service.deleteObjectsByUrls(deleteUrls);

			imageRepository.deleteAllInBatch(imagesToDelete);
		}
	}

	@Override
	public void deleteAllImages(ImageType imageType, String referenceCode) {

		long imagesDeleteCount = imageRepository.deleteByImageTypeAndReferenceCode(imageType, referenceCode);

		if (imagesDeleteCount > 0) {
			s3Service.deleteAllObjectsByIdentifier(imageType, referenceCode);
		}
	}

	@Override
	public void deleteTargetImages(ImageType imageType, String referenceCode, List<String> urls) {

		long imagesDeleteCount = imageRepository.deleteImagesByReferencesAndImageUrls(imageType, referenceCode, urls);

		if (imagesDeleteCount > 0) {
			s3Service.deleteObjectsByUrls(urls);
		}
	}

	@Override
	public String compensateToS3Upload(ImageType imageType, String referenceCode, List<String> urls) {

		if (!CollectionUtils.isEmpty(urls)) {
			s3Service.deleteObjectsByUrls(urls);
		}

		return imageRepository.findAllByImageTypeAndReferenceCode(imageType, referenceCode).stream()
			.map(ImageEntity::getImageUrl)
			.findFirst()
			.orElse("");
	}

	@Override
	public String compensateUpload(ImageType imageType, String referenceCode, List<String> urls) {

		if (!CollectionUtils.isEmpty(urls)) {
			List<ImageEntity> images =
				imageRepository.findAllByImageTypeAndReferenceCodeAndImageUrlIn(imageType, referenceCode, urls);

			s3Service.deleteObjectsByUrls(urls);

			imageRepository.deleteAllInBatch(images);
		}

		return imageRepository.findAllByImageTypeAndReferenceCode(imageType, referenceCode).stream()
			.map(ImageEntity::getImageUrl)
			.findFirst()
			.orElse("");
	}
}
