package io.devground.dbay.domain.image.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ImageType;
import io.devground.dbay.common.aws.s3.S3Service;
import io.devground.dbay.domain.image.entity.Image;
import io.devground.dbay.domain.image.mapper.ImageMapper;
import io.devground.dbay.domain.image.repository.ImageRepository;
import io.devground.dbay.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;
	private final S3Service s3Service;

	@Override
	public void saveImages(ImageType imageType, String referenceCode, List<String> urls) {

		List<Image> images = urls.stream()
			.map(url -> ImageMapper.of(imageType, referenceCode, url))
			.toList();

		imageRepository.saveAll(images);
	}

	@Override
	public void saveImage(ImageType imageType, String referenceCode, String url) {

		imageRepository.save(ImageMapper.of(imageType, referenceCode, url));
	}

	@Override
	@Transactional(readOnly = true)
	public String getImageByCode(ImageType imageType, String referenceCode) {

		Image image = imageRepository.findByImageTypeAndReferenceCode(imageType, referenceCode);

		return image != null ? image.getImageUrl() : "";
	}

	@Override
	public void deleteImageByReferences(ImageType imageType, String referenceCode) {

		long imageDeleteCount = imageRepository.deleteImageByImageTypeAndReferenceCode(imageType, referenceCode);

		if (imageDeleteCount > 0) {
			s3Service.deleteAllObjectsByIdentifier(imageType, referenceCode);
		}
	}

	@Override
	public void deleteImagesByReferencesAndUrl(ImageType imageType, String referenceCode, String url) {

		long imageDeleteCount = StringUtils.isNotBlank(url)
			? imageRepository.deleteImageByReferenceCodeAndImageTypeAndImageUrl(referenceCode, imageType, url)
			: 0;

		if (imageDeleteCount > 0) {
			s3Service.deleteObjectByUrl(url);
		}
	}
}
