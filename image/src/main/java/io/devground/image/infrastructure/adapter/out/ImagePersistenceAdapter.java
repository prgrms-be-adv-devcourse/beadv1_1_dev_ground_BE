package io.devground.image.infrastructure.adapter.out;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import io.devground.core.model.vo.ImageType;
import io.devground.image.application.persistence.ImagePersistencePort;
import io.devground.image.domain.model.Image;
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
	public List<Image> getImages(ImageType imageType, String referenceCode) {

		return imageRepository.findAllByImageTypeAndReferenceCode(imageType, referenceCode).stream()
			.map(ImageMapper::toImageDomain)
			.toList();
	}

	@Override
	public List<Image> getTargetImages(ImageType imageType, String referenceCode, List<String> urls) {

		return imageRepository.findAllByImageTypeAndReferenceCodeAndImageUrlIn(imageType, referenceCode, urls).stream()
			.map(ImageMapper::toImageDomain)
			.toList();
	}

	@Override
	public void saveImages(List<Image> images) {

		List<ImageEntity> newImages = images.stream()
			.map(ImageMapper::toImageEntity)
			.toList();

		imageRepository.saveAll(newImages);
	}

	@Override
	public void deleteAllImages(List<Image> images) {

		List<ImageEntity> imageEntities = images.stream()
			.map(ImageMapper::toImageEntity)
			.toList();

		imageRepository.deleteAllInBatch(imageEntities);

		List<String> urls = images.stream()
			.map(image -> image.getImageSpec().imageUrl())
			.toList();

		if (!urls.isEmpty()) {
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
