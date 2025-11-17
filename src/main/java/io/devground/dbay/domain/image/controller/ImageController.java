package io.devground.dbay.domain.image.controller;

import static org.springframework.http.HttpStatus.*;

import java.net.URL;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.dto.image.DeleteImagesRequest;
import io.devground.core.dto.image.GeneratePresignedRequest;
import io.devground.core.dto.image.UpdateImagesRequest;
import io.devground.core.model.vo.ImageType;
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

	@PostMapping(value = "/upload")
	public BaseResponse<List<URL>> generatePresignedUrls(
		@RequestBody GeneratePresignedRequest request
	) {

		return BaseResponse.success(
			OK.value(),
			imageService.generatePresignedUrls(request.imageType(), request.referenceCode(), request.fileExtensions()),
			"PresignedURL 목록이 발급되었습니다."
		);
	}

	@GetMapping("/{referenceCode}")
	public BaseResponse<List<String>> getImages(
		@PathVariable String referenceCode,
		@RequestParam ImageType imageType
	) {

		return BaseResponse.success(
			OK.value(),
			imageService.getImagesByCode(imageType, referenceCode),
			"이미지 목록이 발급되었습니다."
		);
	}

	@PostMapping(value = "/update")
	public BaseResponse<List<URL>> updateImages(
		@RequestBody UpdateImagesRequest request
	) {

		return BaseResponse.success(
			OK.value(),
			imageService.updateUrls(request.imageType(), request.referenceCode(), request.deleteUrls(),
				request.newImageExtensions()),
			"상품 이미지 수정이 완료되었습니다. PresignedURL 목록을 확인하세요."
		);
	}

	@DeleteMapping(value = "/compensate-upload")
	public BaseResponse<Void> compensateUpload(
		@RequestBody DeleteImagesRequest request
	) {

		return BaseResponse.success(
			NO_CONTENT.value(),
			imageService.compensateUpload(request.imageType(), request.referenceCode())
		);
	}
}
