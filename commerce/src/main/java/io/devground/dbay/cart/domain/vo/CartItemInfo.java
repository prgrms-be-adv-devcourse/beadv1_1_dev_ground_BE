package io.devground.dbay.cart.domain.vo;

public record CartItemInfo(
        String productCode,
        String thumbnail,
        String productName,
        long productPrice
) {
}
