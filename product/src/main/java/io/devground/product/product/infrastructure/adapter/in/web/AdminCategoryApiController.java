package io.devground.product.product.infrastructure.adapter.in.web;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.domain.port.in.AdminCategoryUseCase;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.CategoryTreeResponse;
import io.devground.product.product.infrastructure.model.web.request.RegistCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@Tag(name = "AdminCategoryController", description = "관리자용 카테고리 API")
public class AdminCategoryApiController {

	private final AdminCategoryUseCase categoryService;

	@PostMapping
	@Operation(summary = "카테고리 등록", description = "관리자는 새로운 카테고리를 등록할 수 있습니다.")
	public BaseResponse<AdminCategoryResponse> registCategory(
		@RequestBody @Valid RegistCategoryRequest request) {

		RegistCategoryDto requestDto = new RegistCategoryDto(request.name(), request.parentId());

		return BaseResponse.success(
			CREATED.value(),
			categoryService.registCategory(requestDto),
			"카테고리가 등록되었습니다."
		);
	}

	@GetMapping("/tree")
	@Operation(summary = "전체 카테고리 트리 형식으로 조회", description = "관리자는 전체 카테고리를 한 눈에 확인할 수 있습니다.")
	public BaseResponse<List<CategoryTreeResponse>> getCategoryTree() {

		return BaseResponse.success(
			OK.value(),
			categoryService.getCategoryTree(),
			"카테고리 트리를 조회합니다."
		);
	}
}
