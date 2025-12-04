package io.devground.dbay.cart.domain.vo;

import java.util.List;

public record CartDescription(
        CartCode cartCode,
        List<CartItemInfo> cartItemInfos,
        long totalPrice
) {
}
