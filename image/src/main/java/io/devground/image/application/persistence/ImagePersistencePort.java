package io.devground.image.application.persistence;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;

public interface ImagePersistencePort {

	List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions);
}
