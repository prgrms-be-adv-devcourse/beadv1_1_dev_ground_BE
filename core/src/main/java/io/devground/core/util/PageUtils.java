package io.devground.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final Sort DEFAULT_SORT = Sort.by("createdAt").descending();

	private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
		"createdAt",
		"price"
	);

	public Pageable convertToSafePageable(Pageable pageable) {

		int rawSize = pageable.getPageSize();

		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int size = rawSize > 0 ? rawSize : DEFAULT_PAGE_SIZE;

		Sort sort = convertToSafeSort(pageable.getSort());

		return PageRequest.of(page, size, sort);
	}

	private Sort convertToSafeSort(Sort sort) {

		if (sort == null || sort.isUnsorted()) {
			return DEFAULT_SORT;
		}

		List<Sort.Order> orders = new ArrayList<>();
		boolean isInvalid = false;

		for (Sort.Order order : sort) {
			String property = order.getProperty();

			if (!ALLOWED_SORT_PROPERTIES.contains(property)) {
				isInvalid = true;
				break;
			}

			if ("createdAt".equals(property)) {
				orders.add(order);
				continue;
			}

			if ("price".equals(property)) {
				orders.add(new Sort.Order(order.getDirection(), "productSale.price"));
			}
		}

		if (isInvalid || orders.isEmpty()) {
			return DEFAULT_SORT;
		}

		return Sort.by(orders);
	}
}
