package io.devground.dbay.domain.product.category.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;
import io.devground.dbay.domain.product.category.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@Tag(name = "AdminCategoryController", description = "관리자용 카테고리 API")
public class AdminCategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public BaseResponse<AdminCategoryResponse> registCategory(
		@RequestBody @Valid RegistCategoryRequest request) {

		return BaseResponse.success(
			CREATED.value(),
			categoryService.registCategory(request),
			"카테고리가 등록되었습니다."
		);
	}
}
