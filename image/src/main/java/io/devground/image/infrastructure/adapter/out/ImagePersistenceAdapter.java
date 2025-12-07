package io.devground.image.infrastructure.adapter.out;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Repository;

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
}
