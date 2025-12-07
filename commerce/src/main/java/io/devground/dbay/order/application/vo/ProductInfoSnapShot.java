package io.devground.dbay.order.application.vo;

import io.devground.dbay.cart.domain.vo.ProductCode;

public record ProductInfoSnapShot(
        ProductCode productCode,
        String productSaleCode,
        String sellerCode,
        String title,
        long price
) {
}
