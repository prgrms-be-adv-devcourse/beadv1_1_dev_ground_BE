package io.devground.dbay.order.domain.vo;

public record OrderItemInfo(
        String code,
        String productName,
        Long productPrice
) {
}
