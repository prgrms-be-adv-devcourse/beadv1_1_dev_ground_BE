package io.devground.dbay.cart.application.vo;

import java.util.List;

public record ProductInfos(
        long totalAmount,
        List<ProductInfoSnapShot> productInfoSnapShots
) {
}
