package io.devground.dbay.cart.infrastructure.adapter.in.vo;

public record AddCartItemResponse(
        String cartCode,
        String productCode
) {
}
