package io.devground.dbay.domain.image.validation;

import java.util.List;

import org.springframework.util.StringUtils;

import io.devground.core.model.vo.ErrorCode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageFileValidator {

	private static final List<String> ALLOWED_EXTENSIONS = List.of("png", "jpg", "jpeg", "webp");

	public void validateFileExtension(String fileExtension) {

		if (!StringUtils.hasText(fileExtension)) {
			return;
		}

		if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
			ErrorCode.INVALID_FILE_EXTENSION.throwServiceException();
		}
	}
}
