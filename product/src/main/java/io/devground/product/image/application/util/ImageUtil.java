package io.devground.product.image.application.util;

import java.util.List;

import io.devground.core.model.vo.ImageType;
import io.devground.core.util.CodeUtil;
import io.devground.product.image.domain.vo.ImageDomainErrorCode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageUtil {

	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(
		"jpg", "jpeg", "png", "gif", "webp", "bmp", "svg"
	);

	public String buildS3Key(ImageType imageType, String referenceCode, String fileExtension) {

		String key = generateKey(fileExtension);

		return switch (imageType) {
			case PRODUCT -> "product/" + referenceCode + key;
		};
	}

	public String extractS3KeyFromUrl(String url) {

		int domainEndIdx = url.indexOf(".com/");

		return domainEndIdx != -1 ? url.substring(domainEndIdx + 5) : null;
	}

	public String extractFileExtensions(String filename) {

		if (filename == null || !filename.contains(".")) {
			return "jpg";
		}

		return filename.substring(filename.lastIndexOf(".") + 1);
	}

	public void validateImageExtension(String extension) {

		if (extension == null || extension.isBlank()) {
			ImageDomainErrorCode.INVALID_IMAGE_EXTENSION.throwException();
		}

		String lowerExtension = extension.toLowerCase();
		if (!ALLOWED_IMAGE_EXTENSIONS.contains(lowerExtension)) {
			ImageDomainErrorCode.INVALID_IMAGE_EXTENSION.throwException();
		}
	}

	public void validateImageExtensions(List<String> extensions) {

		if (extensions == null || extensions.isEmpty()) {
			return;
		}

		extensions.forEach(ImageUtil::validateImageExtension);
	}

	public void validateImageUrl(String url) {

		if (url == null || url.isBlank()) {
			ImageDomainErrorCode.IMAGE_URL_MUST_BE_INPUT.throwException();
		}

		String extension = extractFileExtensions(url);
		validateImageExtension(extension);
	}

	public void validateImageUrls(List<String> urls) {

		if (urls == null || urls.isEmpty()) {
			return;
		}

		urls.forEach(ImageUtil::validateImageUrl);
	}

	public String getFolderPath(ImageType imageType, String referenceCode) {

		String domain = switch (imageType) {
			case PRODUCT -> "product/";
		};

		return domain + referenceCode + "/";
	}

	private String generateKey(String fileExtension) {

		return "/" + CodeUtil.generateUUID() + "." + fileExtension;
	}
}
