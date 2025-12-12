package io.devground.dbay.cart.domain.vo;

public record CartItemInfo(
        ProductCode productCode,
        String thumbnail,
        String productName,
        long productPrice
) {
}
