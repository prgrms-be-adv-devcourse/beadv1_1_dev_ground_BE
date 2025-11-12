package io.devground.dbay.domain.image.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.dto.image.DeleteImagesRequest;
import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "ImageController", description = "이미지 API")
public class ImageController {

	private final ImageService imageService;

	@DeleteMapping(value = "/delete")
	public BaseResponse<Void> deleteAll(
		@RequestBody DeleteImagesRequest request
	) {

		return BaseResponse.success(
			HttpStatus.NO_CONTENT.value(),
			imageService.deleteImagesByReferencesAndUrls(request.imageType(), request.referenceCode(),
				request.deleteUrls())
		);
	}

	// TODO: 배포 시 확인 후 삭제 - 동기로 업로드
/*
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<Void> uploadToS3(
		@RequestPart("images") MultipartFile[] files,
		@RequestBody UploadImagesRequest request
	) {

		return BaseResponse.success(
			HttpStatus.NO_CONTENT.value(),
			imageService.saveImages(request, files)
		);
	}
*/
}
