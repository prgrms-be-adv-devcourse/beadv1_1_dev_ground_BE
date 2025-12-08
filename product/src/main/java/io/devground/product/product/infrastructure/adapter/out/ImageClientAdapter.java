package io.devground.product.product.infrastructure.adapter.out;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import io.devground.core.model.vo.ImageType;
import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.application.port.out.ImageClientPort;
import io.devground.product.product.infrastructure.adapter.out.client.ImageClient;
import io.devground.product.product.infrastructure.model.web.request.ImageUpdatePlan;
import io.devground.product.product.infrastructure.model.web.request.ImageUploadPlan;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageClientAdapter implements ImageClientPort {

	private final ImageClient imageClient;

	@Override
	public List<String> getImageUrls(String productCode, ImageType imageType) {

		BaseResponse<List<String>> urlResponses = imageClient.getImageUrls(productCode, imageType)
			.throwIfNotSuccess();

		return urlResponses.data();
	}

	@Override
	public List<URL> prepareUploadUrls(ImageType imageType, String productCode, List<String> fileExtensions) {

		ImageUploadPlan imageUploadPlan = new ImageUploadPlan(imageType, productCode, fileExtensions);

		BaseResponse<List<URL>> presignedUrlResponses = imageClient.generatePresignedUrls(imageUploadPlan)
			.throwIfNotSuccess();

		return presignedUrlResponses.data();
	}

	@Override
	public List<URL> updateImages(
		ImageType imageType, String productCode, List<String> deleteUrls, List<String> newImageExtensions
	) {

		ImageUpdatePlan imageUpdatePlan = new ImageUpdatePlan(imageType, productCode, deleteUrls, newImageExtensions);

		BaseResponse<List<URL>> newPresignedUrlResponses = imageClient.updateImages(imageUpdatePlan)
			.throwIfNotSuccess();

		return newPresignedUrlResponses.data();
	}
}
