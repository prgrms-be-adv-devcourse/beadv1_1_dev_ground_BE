package io.devground.image.infrastructure.adapter.in.web;

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
import io.devground.image.application.service.ImageApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "ImageController", description = "이미지 API")
public class ImageApiController {

	private final ImageApplicationService imageService;

	@PostMapping(value = "/upload")
	@Operation(summary = "PresignedUrl 발급", description = "클라이언트에게 PresignedUrl을 발급합니다.")
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
	@Operation(summary = "상품 이미지 URL 목록 조회", description = "해당 상품의 이미지 URL 목록을 조회합니다.")
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
	@Operation(summary = "상품 이미지 URL 등록", description = "상품의 이미지 URL을 로컬 DB에 저장합니다.")
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

	@DeleteMapping(value = "compensate-s3")
	@Operation(summary = "S3만 업로드될 시 보상", description = "상품의 S3 작업 과정에서 문제가 발생하면 보상으로 해당 S3 이미지를 삭제합니다.")
	public BaseResponse<String> compensateS3Upload(
		@RequestBody DeleteImagesRequest request
	) {

		return BaseResponse.success(
			OK.value(),
			imageService.compensateToS3Upload(request.imageType(), request.referenceCode(), request.deleteUrls()),
			"보상 처리가 완료되었습니다."
		);
	}

	@DeleteMapping(value = "/compensate-upload")
	@Operation(
		summary = "이미지 업로드 도중 문제 발생 시 보상",
		description = "상품 이미지 업로드 과정에서 문제가 발생하면 보상으로 S3 및 로컬 DB 이미지를 삭제합니다."
	)
	public BaseResponse<String> compensateDbUpload(
		@RequestBody DeleteImagesRequest request
	) {

		return BaseResponse.success(
			OK.value(),
			imageService.compensateUpload(request.imageType(), request.referenceCode(), request.deleteUrls()),
			"보상 처리가 완료되었습니다."
		);
	}
}
