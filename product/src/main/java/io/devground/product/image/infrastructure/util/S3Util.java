package io.devground.product.image.infrastructure.util;

import io.devground.core.model.vo.ImageType;
import io.devground.core.util.CodeUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class S3Util {

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
