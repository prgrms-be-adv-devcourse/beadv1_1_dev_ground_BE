package io.devground.product.product.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.util.CodeUtil;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.CategoryResponse;
import io.devground.product.product.domain.vo.response.CategoryTreeResponse;
import io.devground.product.product.infrastructure.adapter.out.repository.CategoryJpaRepository;
import io.devground.product.product.infrastructure.model.persistence.CategoryEntity;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CategoryApplicationTest {

	@Autowired
	AdminCategoryApplication adminCategoryService;
	@Autowired
	CategoryApplication categoryService;
	@Autowired
	CategoryJpaRepository categoryRepository;

	@BeforeEach
	void init() {
		AdminCategoryResponse responseDepth1 = adminCategoryService.registCategory(
			new RegistCategoryDto("핸드폰", null));

		AdminCategoryResponse responseDepth2 = adminCategoryService.registCategory(
			new RegistCategoryDto("아이폰", responseDepth1.id()));

		adminCategoryService.registCategory(new RegistCategoryDto("아이폰15", responseDepth2.id()));
	}

	@Test
	@DisplayName("성공_카테고리 등록")
	void success_regist_category() throws Exception {

		// given, when
		RegistCategoryDto requestDepth1 = new RegistCategoryDto("노트북", null);
		AdminCategoryResponse responseDepth1 = adminCategoryService.registCategory(requestDepth1);

		RegistCategoryDto requestDepth2 = new RegistCategoryDto("맥OS", responseDepth1.id());
		AdminCategoryResponse responseDepth2 = adminCategoryService.registCategory(requestDepth2);

		RegistCategoryDto requestDepth3 = new RegistCategoryDto("M2", responseDepth2.id());
		AdminCategoryResponse responseDepth3 = adminCategoryService.registCategory(requestDepth3);

		CategoryEntity category1 = categoryRepository.findById(responseDepth1.id()).get();
		CategoryEntity category2 = categoryRepository.findById(responseDepth2.id()).get();
		CategoryEntity category3 = categoryRepository.findById(responseDepth3.id()).get();

		// then
		assertEquals("노트북", category1.getName());
		assertNull(category1.getParent());

		assertEquals("맥OS", category2.getName());
		assertEquals(category1.getId(), category2.getParent().getId());

		assertEquals("M2", category3.getName());
		assertEquals(category2.getId(), category3.getParent().getId());
		assertEquals(category1.getId(), category3.getParent().getParent().getId());
	}

	@Test
	@DisplayName("실패_최하위 카테고리 제한 초과")
	void fail_exceed_depth() throws Exception {

		// given
		AdminCategoryResponse depth1 = adminCategoryService.registCategory(
			new RegistCategoryDto("1", null));
		AdminCategoryResponse depth2 = adminCategoryService.registCategory(
			new RegistCategoryDto("2", depth1.id()));
		AdminCategoryResponse depth3 = adminCategoryService.registCategory(
			new RegistCategoryDto("3", depth2.id()));

		// when, then
		assertThrows(ProductDomainException.class,
			() -> adminCategoryService.registCategory(new RegistCategoryDto("4", depth3.id())));
	}

	@Test
	@DisplayName("실패_depth 미스매치")
	void fail_mismatch_depth() throws Exception {

		// given
		CategoryEntity parent = CategoryEntity.builder()
			.code(CodeUtil.generateUUID())
			.parent(null)
			.name("parent")
			.depth(1)
			.build();
		categoryRepository.save(parent);

		// when, then
		assertThrows(ProductDomainException.class,
			() -> CategoryEntity.builder()
				.parent(parent)
				.name("child")
				.depth(3)
				.build());
	}

	@Test
	@DisplayName("실패_상위 카테고리 미존재")
	void fail_parent_not_found() throws Exception {

		// when, then
		assertThrows(ProductDomainException.class,
			() -> adminCategoryService.registCategory(new RegistCategoryDto("child", 500L)));
	}

	@Test
	@DisplayName("성공_카테고리 트리 조회")
	void success_category_tree() throws Exception {

		// given, when
		List<CategoryTreeResponse> responses = adminCategoryService.getCategoryTree();

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

	@Test
	@DisplayName("성공_최상위 카테고리 조회")
	void success_root_category() throws Exception {

		// given, when
		List<CategoryResponse> rootCategories = categoryService.getRootCategories();
		CategoryResponse rootCategory = rootCategories.getFirst();

		// then
		assertEquals(1, rootCategories.size());
		assertEquals("핸드폰", rootCategory.name());
		assertFalse(rootCategory.isLeaf());
	}

	@Test
	@DisplayName("성공_직속 하위 카테고리 조회")
	void success_child_category() throws Exception {

		// given
		AdminCategoryResponse root = adminCategoryService.registCategory(
			new RegistCategoryDto("루트", null));

		AdminCategoryResponse depth2_1 = adminCategoryService.registCategory(
			new RegistCategoryDto("하위2_1", root.id()));

		AdminCategoryResponse depth2_2 = adminCategoryService.registCategory(
			new RegistCategoryDto("하위2_2", root.id()));

		AdminCategoryResponse depth2_3 = adminCategoryService.registCategory(
			new RegistCategoryDto("하위2_3", root.id()));

		AdminCategoryResponse depth_3_1 = adminCategoryService.registCategory(
			new RegistCategoryDto("하위3_1", depth2_1.id()));

		AdminCategoryResponse depth_3_2 = adminCategoryService.registCategory(
			new RegistCategoryDto("하위3_2", depth2_1.id()));

		// when
		List<CategoryResponse> rootResponses = categoryService.getChildCategories(root.id());
		List<CategoryResponse> childResponses = categoryService.getChildCategories(depth2_1.id());

		// then
		assertEquals(3, rootResponses.size());
		assertEquals("하위2_1", rootResponses.getFirst().name());
		assertEquals("하위2_3", rootResponses.getLast().name());
		assertFalse(rootResponses.getFirst().isLeaf());
		assertTrue(rootResponses.getLast().isLeaf());
		assertEquals("하위3_1", childResponses.getFirst().name());
		assertEquals("하위3_2", childResponses.getLast().name());
		assertTrue(childResponses.getFirst().isLeaf());
		assertTrue(childResponses.getLast().isLeaf());
	}

	@Test
	@DisplayName("실패_하위 카테고리 조회 시 존재하지 않는 카테고리")
	void fail_child_category_non_id() throws Exception {

		// given, when, then
		assertThrows(ServiceException.class,
			() -> categoryService.getChildCategories(300L));
	}
}