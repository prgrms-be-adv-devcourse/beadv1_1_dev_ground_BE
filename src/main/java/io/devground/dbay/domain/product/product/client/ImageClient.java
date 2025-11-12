package io.devground.dbay.domain.product.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.devground.core.dto.image.DeleteImagesRequest;
import io.devground.core.model.web.BaseResponse;

@FeignClient(name = "image", url = "http://localhost:8080", path = "/api/images")
public interface ImageClient {

	@DeleteMapping(value = "/delete")
	BaseResponse<Void> deleteAll(@RequestBody DeleteImagesRequest request);

	// TODO: 배포 시 확인 후 삭제 - 동기로 업로드
/*
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	BaseResponse<Void> uploadToS3(
		@RequestPart("images") MultipartFile[] files,
		@RequestBody UploadImagesRequest request
	);
*/
}
