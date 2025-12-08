package io.devground.product.product.infrastructure.adapter.out.client;

import java.net.URL;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.devground.core.model.vo.ImageType;
import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.infrastructure.model.web.request.DeleteImagesRequest;
import io.devground.product.product.infrastructure.model.web.request.ImageUpdatePlan;
import io.devground.product.product.infrastructure.model.web.request.ImageUploadPlan;

@FeignClient(name = "image", url = "localhost:8080", path = "/api/images")
public interface ImageClient {

	@GetMapping("/{referenceCode}")
	BaseResponse<List<String>> getImageUrls(@PathVariable String referenceCode, @RequestParam ImageType imageType);

	@PostMapping(value = "/upload")
	BaseResponse<List<URL>> generatePresignedUrls(@RequestBody ImageUploadPlan request);

	@PostMapping(value = "/update")
	BaseResponse<List<URL>> updateImages(@RequestBody ImageUpdatePlan request);

	@DeleteMapping(value = "compensate-s3")
	BaseResponse<String> compensateS3Upload(@RequestBody DeleteImagesRequest request);

	@DeleteMapping(value = "/compensate-upload")
	BaseResponse<String> compensateDbUpload(@RequestBody DeleteImagesRequest request);
}
