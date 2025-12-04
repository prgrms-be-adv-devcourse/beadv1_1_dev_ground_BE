package io.devground.dbay.cart.domain.model;

import io.devground.dbay.cart.domain.exception.DomainError;
import io.devground.dbay.cart.domain.utils.DomainUtil;
import io.devground.dbay.cart.domain.vo.CartCode;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;

import java.util.*;

public class Cart {
    private final CartCode cartCode;
    private final UserCode userCode;
    private final List<CartItem> cartItems;

    private void validate(CartCode cartCode, UserCode userCode) {
        if (cartCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (userCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }
    }

    private Cart(CartCode cartCode, UserCode userCode, List<CartItem> cartItems) {
        validate(cartCode, userCode);

        if (cartItems == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        this.cartCode = cartCode;
        this.userCode = userCode;
        this.cartItems = cartItems;
    }

    public static Cart create(UserCode userCode) {
        return new Cart(new CartCode(DomainUtil.generateCode()), userCode, new ArrayList<>());
    }

    public static Cart restore(CartCode cartCode, UserCode userCode, List<CartItem> cartItems) {
        return new Cart(cartCode, userCode, new ArrayList<>(cartItems));
    }

    // 장바구니가 가지는 행동

    // 장바구니 상품 추가
    public void addCartItem(ProductCode productCode) {
        if (productCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        boolean exists = cartItems.stream().anyMatch(cartItem -> cartItem.getProductCode().equals(productCode));
        if (exists) {
            throw DomainError.CART_ITEM_ALREADY_EXIST.throwDomainException();
        }
        cartItems.add(CartItem.of(productCode, this.cartCode));
    }

    // 개별 삭제
    public void removeCartItem(ProductCode productCode) {
        if (productCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        cartItems.removeIf(cartItem -> cartItem.getProductCode().equals(productCode));
    }

    // 선택 삭제
    public void removeCartItems(List<ProductCode> productCodes) {
        if (productCodes == null || productCodes.isEmpty()) {
            throw DomainError.CART_ITEM_DELETE_NOT_SELECTED.throwDomainException();
        }

        if (productCodes.stream().anyMatch(Objects::isNull)) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        Set<ProductCode> targetItems = new HashSet<>(productCodes);
        cartItems.removeIf(cartItem -> targetItems.contains(cartItem.getProductCode()));
    }

    // 전체 삭제
    public void removeAllCartItems() {
        cartItems.clear();
    }

    public CartCode getCartCode() {
        return cartCode;
    }

    public UserCode getUserCode() {
        return userCode;
    }

    public List<CartItem> getCartItems() {
        return List.copyOf(cartItems);
    }
}
