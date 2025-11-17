package io.devground.dbay.domain.product.product.client;

import java.net.URL;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.devground.core.dto.image.DeleteImagesRequest;
import io.devground.core.dto.image.GeneratePresignedRequest;
import io.devground.core.dto.image.UpdateImagesRequest;
import io.devground.core.model.vo.ImageType;
import io.devground.core.model.web.BaseResponse;

@FeignClient(name = "image", url = "http://localhost:8080", path = "/api/images")
public interface ImageClient {

	@GetMapping("/{referenceCode}")
	BaseResponse<List<String>> getImages(@PathVariable String referenceCode, @RequestParam ImageType imageType);

	@PostMapping(value = "/upload")
	BaseResponse<List<URL>> generatePresignedUrls(@RequestBody GeneratePresignedRequest request);

	@PostMapping(value = "/update")
	BaseResponse<List<URL>> updateImages(@RequestBody UpdateImagesRequest request);

	@DeleteMapping(value = "/compensate-upload")
	BaseResponse<Void> compensateUpload(@RequestBody DeleteImagesRequest request);
}
