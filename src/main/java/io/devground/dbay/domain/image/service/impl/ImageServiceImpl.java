package io.devground.dbay.domain.image.service.impl;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
	public List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions) {

		return s3Service.generatePresignedUrls(imageType, referenceCode, fileExtensions);
	}

	@Override
	public String saveImages(ImageType imageType, String referenceCode, List<String> urls) {

		if (CollectionUtils.isEmpty(urls)) {
			return null;
		}

		List<String> foundUrls = imageRepository.findAllByImageTypeAndReferenceCode(imageType, referenceCode).stream()
			.map(Image::getImageUrl)
			.toList();

		List<String> newUrls = urls.stream()
			.distinct()
			.filter(url -> !foundUrls.contains(url))
			.toList();

		if (!newUrls.isEmpty()) {
			List<Image> newImages = newUrls.stream()
				.map(url -> ImageMapper.of(imageType, referenceCode, url))
				.toList();

			imageRepository.saveAll(newImages);
		}

		return foundUrls.isEmpty()
			? newUrls.getFirst()
			: foundUrls.getFirst();
	}

	@Override
	public List<URL> updateUrls(
		ImageType imageType, String referenceCode, List<String> deleteUrls, List<String> newImageExtensions
	) {

		if (!CollectionUtils.isEmpty(deleteUrls)) {
			List<Image> imagesToDelete =
				imageRepository.findAllByImageTypeAndReferenceCodeAndImageUrlIn(imageType, referenceCode, deleteUrls);

			if (!imagesToDelete.isEmpty()) {
				s3Service.deleteObjectsByUrls(deleteUrls);

				imageRepository.deleteAllInBatch(imagesToDelete);
			}
		}

		return this.generatePresignedUrls(imageType, referenceCode, newImageExtensions);
	}

	@Override
	@Transactional(readOnly = true)
	public String getImageByCode(ImageType imageType, String referenceCode) {

		Image image = imageRepository.findByImageTypeAndReferenceCode(imageType, referenceCode);

		return image != null ? image.getImageUrl() : "";
	}

	@Override
	public void deleteImageByReferences(ImageType imageType, String referenceCode) {

		long imageDeleteCount = imageRepository.deleteByImageTypeAndReferenceCode(imageType, referenceCode);

		if (imageDeleteCount > 0) {
			s3Service.deleteAllObjectsByIdentifier(imageType, referenceCode);
		}
	}

	@Override
	public void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls) {

		if (CollectionUtils.isEmpty(urls)) {
			return;
		}

		long cnt = imageRepository.deleteImagesByReferencesAndImageUrls(imageType, referenceCode, urls);

		if (cnt > 0) {
			s3Service.deleteObjectsByUrls(urls);
		}
	}

	@Override
	public Void compensateUpload(ImageType imageType, String referenceCode) {

		List<Image> images = imageRepository.findAllByImageTypeAndReferenceCode(imageType, referenceCode);

		if (!CollectionUtils.isEmpty(images)) {
			s3Service.deleteAllObjectsByIdentifier(imageType, referenceCode);

			imageRepository.deleteAllInBatch(images);
		}

		return null;
	}
}
