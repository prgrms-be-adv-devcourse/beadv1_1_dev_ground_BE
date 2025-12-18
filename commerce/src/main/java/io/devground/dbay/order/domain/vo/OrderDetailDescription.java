package io.devground.dbay.order.domain.vo;

import java.time.LocalDateTime;

public record OrderDetailDescription(
        String orderCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        OrderStatus orderStatus,
        Long totalAmount,
        Long discount,
        Long productTotalAmount,
        int shippingFee,
        String nickname,
        String address,
        String addressDetail,
        boolean cancellable
) {
}
