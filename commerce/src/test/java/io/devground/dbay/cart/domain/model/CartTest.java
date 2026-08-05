package io.devground.dbay.cart.domain.model;

import io.devground.dbay.cart.domain.exception.DomainError;
import io.devground.dbay.cart.domain.exception.DomainException;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    @Test
    void addCartItemReturnsTheItemOwnedByTheCart() {
        Cart cart = Cart.create(userCode());
        ProductCode productCode = productCode();

        CartItem added = cart.addCartItem(productCode);

        assertThat(added.getCartCode()).isEqualTo(cart.getCartCode());
        assertThat(added.getProductCode()).isEqualTo(productCode);
        assertThat(cart.getCartItems()).containsExactly(added);
    }

    @Test
    void addCartItemRejectsDuplicateProducts() {
        Cart cart = Cart.create(userCode());
        ProductCode productCode = productCode();
        cart.addCartItem(productCode);

        assertThatThrownBy(() -> cart.addCartItem(productCode))
                .isInstanceOf(DomainException.class)
                .extracting(exception -> ((DomainException) exception).getErrorCode())
                .isEqualTo(DomainError.CART_ITEM_ALREADY_EXIST);
    }

    private UserCode userCode() {
        return new UserCode(UUID.randomUUID().toString());
    }

    private ProductCode productCode() {
        return new ProductCode(UUID.randomUUID().toString());
    }
}
