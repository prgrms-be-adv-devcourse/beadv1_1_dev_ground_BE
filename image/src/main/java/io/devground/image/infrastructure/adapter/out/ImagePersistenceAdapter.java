package io.devground.image.infrastructure.adapter.out;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.ImageType;
import io.devground.image.application.persistence.ImagePersistencePort;
import io.devground.image.infrastructure.adapter.out.s3.S3Service;
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
}
