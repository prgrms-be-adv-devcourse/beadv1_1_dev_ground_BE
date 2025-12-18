package io.devground.dbay.domain.product.category.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.product.category.model.dto.CategoryResponse;
import io.devground.dbay.domain.product.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "CategoryController", description = "카테고리 API")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	@Operation(summary = "최상위 카테고리 조회", description = "최상위 카테고리를 조회합니다.")
	public BaseResponse<List<CategoryResponse>> getRootCategories() {

		return BaseResponse.success(
			OK.value(),
			categoryService.getRootCategories(),
			"최상위 카테고리 목록 조회에 성공하였습니다."
		);
	}

	@GetMapping("/{parentId}/children")
	@Operation(summary = "하위 카테고리 조회", description = "해당 상위 카테고리의 직속 하위 카테고리를 조회합니다.")
	public BaseResponse<List<CategoryResponse>> getChildCategories(@PathVariable Long parentId) {

		return BaseResponse.success(
			OK.value(),
			categoryService.getChildCategories(parentId),
			"해당 카테고리의 한 단계 하위 카테고리 목록 조회에 성공하였습니다."
		);
	}
}
