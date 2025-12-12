package io.devground.dbay.cart.domain.port.in;

import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;
import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.model.CartItem;
import io.devground.dbay.cart.domain.vo.CartDescription;
import io.devground.dbay.cart.domain.vo.CartItemInfo;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;

import java.util.List;

public interface CartUseCase {
    Cart create(UserCode userCode);
    CartItem addCartItem(UserCode userCode, ProductCode productCode);
    void removeCartItem(UserCode userCode, ProductCode productCode);
    void removeCartItems(UserCode userCode, List<ProductCode> productCodes);
    void removeAllCartItems(UserCode userCode);
    void deleteCart(UserCode userCode);
    CartDescription getCartInfos(UserCode userCode);
    List<CartItemInfo> recommendProductsByCartItem(UserCode userCode);
}
