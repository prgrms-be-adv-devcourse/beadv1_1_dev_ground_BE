package io.devground.dbay.common.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.ddddeposit.domain.pagination.PageQuery;
import io.devground.dbay.ddddeposit.domain.pagination.SortSpec;

@Component
public class PageQueryArgumentResolver implements HandlerMethodArgumentResolver {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 10;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		return PageQuery.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		String pageParam = webRequest.getParameter("page");
		String sizeParam = webRequest.getParameter("size");
		String sortParam = webRequest.getParameter("sort");

		int page = convertToSafeParam(pageParam, DEFAULT_PAGE);
		int size = convertToSafeParam(sizeParam, DEFAULT_SIZE);

		SortSpec sortSpec = convertToSortSpec(sortParam);

		return new PageQuery(page, size, sortSpec);
	}

	private int convertToSafeParam(String param, int defaultValue) {

		try {
			return Integer.parseInt(param);
		} catch (NumberFormatException e) {
			return defaultValue;
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.PARAMETER_INVALID);
		}
	}

	private SortSpec convertToSortSpec(String sortParam) {

		if (!StringUtils.hasText(sortParam)) {
			return null;
		}

		String[] sortBits = sortParam.split(",");

		if (sortBits.length == 0 || sortBits[0].isBlank()) {
			return null;
		}

		String property = sortBits[0];

		SortSpec.Direction direction = null;
		if (sortBits.length > 1) {
			String sortDir = sortBits[1].trim();

			if (sortDir.equalsIgnoreCase("asc")) {
				direction = SortSpec.Direction.ASC;
			} else {
				direction = SortSpec.Direction.DESC;
			}
		}

		return new SortSpec(property, direction);
	}
}
