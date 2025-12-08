package io.devground.product.product.infrastructure.mapper;

import org.springframework.data.domain.Page;

import io.devground.product.product.domain.vo.pagination.PageDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PageMapper {

	public <T> PageDto<T> from(Page<T> page) {
		return new PageDto<>(
			page.getNumber() + 1,
			page.getSize(),
			page.getTotalPages(),
			page.getTotalElements(),
			page.getContent()
		);
	}
}
