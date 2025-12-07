package io.devground.dbay.order.infrastructure.mapper;

import io.devground.dbay.order.domain.vo.pagination.PageQuery;
import io.devground.dbay.order.domain.vo.pagination.SortSpec;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageMapper {
    public static Pageable toPageable(PageQuery pageQuery) {
        SortSpec sort = pageQuery.sort();

        String property = switch (sort.property()) {
            case "updatedAt" -> "updatedAt";
            case "totalAmount" -> "totalAmount";
            default -> "createdAt";
        };

        Sort.Direction dir = sort.direction() == SortSpec.Direction.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(dir, property));
    }
}
