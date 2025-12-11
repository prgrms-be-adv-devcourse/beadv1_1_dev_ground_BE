package io.devground.dbay.order.domain.vo;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDescription(
        String code,
        String userCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long totalAmount,
        OrderStatus orderStatus,
        List<OrderItemInfo> orderItemInfos
) {
}
