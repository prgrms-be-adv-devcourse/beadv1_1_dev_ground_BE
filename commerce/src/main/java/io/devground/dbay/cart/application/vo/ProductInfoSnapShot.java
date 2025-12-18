package io.devground.dbay.cart.application.vo;

import io.devground.dbay.cart.domain.vo.ProductCode;

public record ProductInfoSnapShot(
        ProductCode productCode,
        String productSaleCode,
        String sellerCode,
        String title,
        String thumbnail,
        long price,
        String description,
        String categoryName
) {
}