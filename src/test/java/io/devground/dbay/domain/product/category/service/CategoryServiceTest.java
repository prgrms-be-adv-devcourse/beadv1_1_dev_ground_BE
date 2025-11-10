package io.devground.dbay.domain.product.category.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.devground.core.model.exception.ServiceException;
import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryTreeResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;
import io.devground.dbay.domain.product.category.entity.Category;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceTest {

	@Autowired
	CategoryService categoryService;
	@Autowired
	CategoryRepository categoryRepository;

	@Test
	@DisplayName("성공_카테고리 등록")
	void success_regist_category() throws Exception {

		RegistCategoryRequest requestDepth1 = new RegistCategoryRequest("핸드폰", null);
		AdminCategoryResponse responseDepth1 = categoryService.registCategory(requestDepth1);

		RegistCategoryRequest requestDepth2 = new RegistCategoryRequest("아이폰", responseDepth1.id());
		AdminCategoryResponse responseDepth2 = categoryService.registCategory(requestDepth2);

		RegistCategoryRequest requestDepth3 = new RegistCategoryRequest("아이폰15", responseDepth2.id());
		AdminCategoryResponse responseDepth3 = categoryService.registCategory(requestDepth3);

		Category category1 = categoryRepository.findById(responseDepth1.id()).get();
		Category category2 = categoryRepository.findById(responseDepth2.id()).get();
		Category category3 = categoryRepository.findById(responseDepth3.id()).get();

		assertEquals("핸드폰", category1.getName());
		assertNull(category1.getParent());

		assertEquals("아이폰", category2.getName());
		assertEquals(category1.getId(), category2.getParent().getId());

		assertEquals("아이폰15", category3.getName());
		assertEquals(category2.getId(), category3.getParent().getId());
		assertEquals(category1.getId(), category3.getParent().getParent().getId());
	}

	@Test
	@DisplayName("실패_최하위 카테고리 제한 초과")
	void fail_exceed_depth() throws Exception {
		AdminCategoryResponse depth1 = categoryService.registCategory(
			new RegistCategoryRequest("1", null));
		AdminCategoryResponse depth2 = categoryService.registCategory(
			new RegistCategoryRequest("2", depth1.id()));
		AdminCategoryResponse depth3 = categoryService.registCategory(
			new RegistCategoryRequest("3", depth2.id()));
		AdminCategoryResponse depth4 = categoryService.registCategory(
			new RegistCategoryRequest("4", depth3.id()));
		AdminCategoryResponse depth5 = categoryService.registCategory(
			new RegistCategoryRequest("5", depth4.id()));

		assertThrows(ServiceException.class,
			() -> categoryService.registCategory(new RegistCategoryRequest("6", depth5.id())));
	}

	@Test
	@DisplayName("실패_depth 미스매치")
	void fail_mismatch_depth() throws Exception {
		Category parent = Category.builder()
			.parent(null)
			.name("parent")
			.depth(1)
			.build();
		categoryRepository.save(parent);

		assertThrows(ServiceException.class,
			() -> Category.builder()
				.parent(parent)
				.name("child")
				.depth(3)
				.build());
	}

	@Test
	@DisplayName("실패_상위 카테고리 미존재")
	void fail_parent_not_found() throws Exception {
		assertThrows(ServiceException.class,
			() -> categoryService.registCategory(new RegistCategoryRequest("child", 500L)));
	}

	@Test
	@DisplayName("성공_카테고리 트리 조회")
	void success_category_tree() throws Exception {

		// given
		RegistCategoryRequest requestDepth1 = new RegistCategoryRequest("핸드폰", null);
		AdminCategoryResponse responseDepth1 = categoryService.registCategory(requestDepth1);

		RegistCategoryRequest requestDepth2 = new RegistCategoryRequest("아이폰", responseDepth1.id());
		AdminCategoryResponse responseDepth2 = categoryService.registCategory(requestDepth2);

		RegistCategoryRequest requestDepth3 = new RegistCategoryRequest("아이폰15", responseDepth2.id());
		AdminCategoryResponse responseDepth3 = categoryService.registCategory(requestDepth3);

		// when
		List<CategoryTreeResponse> responses = categoryService.getCategoryTree();

		CategoryTreeResponse root = responses.getFirst();
		CategoryTreeResponse childDepth2 = root.children().getFirst();
		CategoryTreeResponse childDepth3 = childDepth2.children().getFirst();

		// then
		assertEquals("핸드폰", root.name());
		assertEquals(1, root.depth());
		assertFalse(root.isLeaf());
		assertEquals("아이폰", childDepth2.name());
		assertEquals(2, childDepth2.depth());
		assertFalse(childDepth2.isLeaf());
		assertEquals("아이폰15", childDepth3.name());
		assertEquals(3, childDepth3.depth());
		assertTrue(childDepth3.isLeaf());
	}
}