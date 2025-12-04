package io.devground.dbay.cart.application.port.out.persistence;

import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.model.CartItem;
import io.devground.dbay.cart.domain.vo.CartCode;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;


import java.util.List;
import java.util.Optional;

public interface CartPersistencePort {
    Cart saveCart(Cart cart);
    CartItem saveCartItem(CartCode cartCode, ProductCode productCode);
    Optional<Cart> getCart(UserCode userCode);
    void removeCartItem(CartCode cartCode, ProductCode productCode);
    void removeCartItems(CartCode cartCode, List<ProductCode> productCodes);
    void removeAllCartItems(CartCode cartCode);
    void removeCart(UserCode userCode);
}
