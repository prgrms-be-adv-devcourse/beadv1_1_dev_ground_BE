package io.devground.dbay.domain.image.service.impl;

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

	// TODO: 사용하지 않을 시 삭제
/*
	@Override
	public Void saveImages(UploadImagesRequest request, MultipartFile[] files) {

		ImageType imageType = request.imageType();
		String referenceCode = request.referenceCode();

		List<String> urls = s3Service.uploadFiles(request.imageType(), request.referenceCode(), files);

		if (!urls.isEmpty()) {
			this.saveImages(imageType, referenceCode, urls);
		}

		return null;
	}
*/

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
	public Void deleteImagesByReferencesAndUrls(ImageType imageType, String referenceCode, List<String> urls) {

		if (CollectionUtils.isEmpty(urls)) {
			return null;
		}

		long cnt = imageRepository.deleteImagesByReferencesAndImageUrls(imageType, referenceCode, urls);

		if (cnt > 0) {
			s3Service.deleteObjectsByUrls(urls);
		}

		return null;
	}
}
