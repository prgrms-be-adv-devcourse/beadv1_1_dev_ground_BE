package io.devground.dbay.common.aws.s3;

import java.util.UUID;

import io.devground.dbay.domain.image.vo.ImageType;
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

	public String getFolderPath(ImageType imageType, String referenceCode) {

		String domain = switch (imageType) {
			case PRODUCT -> "product/";
		};

		return domain + referenceCode + "/";
	}

	private String generateKey(String fileExtension) {

		return "/" + UUID.randomUUID() + "." + fileExtension;
	}
}
