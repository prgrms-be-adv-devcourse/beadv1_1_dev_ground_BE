package io.devground.product.infrastructure.adapter.out;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import io.devground.core.model.vo.ImageType;
import io.devground.core.model.web.BaseResponse;
import io.devground.product.application.model.vo.ApplicationImageType;
import io.devground.product.application.port.out.ImagePort;
import io.devground.product.infrastructure.adapter.out.client.ImageClient;
import io.devground.product.infrastructure.model.web.request.GeneratePresignedRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageAdapter implements ImagePort {

	private final ImageClient imageClient;

	@Override
	public List<String> getImageUrls(String productCode, ApplicationImageType applicationImageType) {
		ImageType imageType = ImageType.fromName(applicationImageType.name());

		BaseResponse<List<String>> urlResponses = imageClient.getImageUrls(productCode, imageType)
			.throwIfNotSuccess();

		return urlResponses.data();
	}

	@Override
	public List<URL> generatePresignedUrls(GeneratePresignedRequest request) {

		BaseResponse<List<URL>> presignedUrlResponses = imageClient.generatePresignedUrls(request)
			.throwIfNotSuccess();

		return presignedUrlResponses.data();
	}
}
