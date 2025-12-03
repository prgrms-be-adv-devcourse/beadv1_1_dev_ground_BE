package io.devground.product.infrastructure.util;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.pagination.SortSpec;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final Sort DEFAULT_SORT = Sort.by("createdAt").descending();

	private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
		"createdAt", "price"
	);

	public Pageable convertToSafePageable(PageQuery pageable) {

		int rawSize = pageable.size();

		int page = pageable.page();
		int size = rawSize > 0 ? rawSize : DEFAULT_PAGE_SIZE;

		Sort sort = convertToSafeSort(pageable.sort());

		return PageRequest.of(page, size, sort);
	}

	private Sort convertToSafeSort(SortSpec sort) {

		if (sort == null) {
			return DEFAULT_SORT;
		}

		String property = sort.property();

		if (!ALLOWED_SORT_PROPERTIES.contains(property)) {
			return DEFAULT_SORT;
		}

		Sort.Direction direction = Sort.Direction.DESC;
		if (sort.direction() != null) {
			direction = sort.direction() == SortSpec.Direction.ASC
				? Sort.Direction.ASC
				: Sort.Direction.DESC;
		}

		if ("price".equals(property)) {
			return Sort.by(new Sort.Order(direction, "productSaleEntity.price"));
		}

		if ("createdAt".equals(property)) {
			return Sort.by(new Sort.Order(direction, property));
		}

		return DEFAULT_SORT;
	}
}
