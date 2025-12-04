package io.devground.dbay.cart.domain.model;

import io.devground.dbay.cart.domain.exception.DomainError;
import io.devground.dbay.cart.domain.vo.CartCode;
import io.devground.dbay.cart.domain.vo.ProductCode;

public class CartItem {
    private final ProductCode productCode;
    private final CartCode cartCode;

    private CartItem(ProductCode productCode, CartCode cartCode) {
        if (productCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (cartCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        this.productCode = productCode;
        this.cartCode = cartCode;
    }

    public static CartItem of(ProductCode productCode, CartCode cartCode) {
        return new CartItem(productCode, cartCode);
    }

    public ProductCode getProductCode() {
        return productCode;
    }

    public CartCode getCartCode() {
        return cartCode;
    }
}
