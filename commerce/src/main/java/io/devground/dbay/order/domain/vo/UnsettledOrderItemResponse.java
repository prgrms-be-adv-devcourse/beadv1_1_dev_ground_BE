package io.devground.dbay.order.domain.vo;

public record UnsettledOrderItemResponse(
        String orderCode,
        String userCode,
        String orderItemCode,
        String sellerCode,
        Long productPrice
) {
}
