package io.devground.dbay.cart.application.service;

import io.devground.dbay.cart.application.exception.ServiceError;
import io.devground.dbay.cart.application.port.out.product.CartProductPort;
import io.devground.dbay.cart.application.port.out.persistence.CartPersistencePort;
import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;
import io.devground.dbay.cart.application.vo.ProductSnapShot;
import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.model.CartItem;
import io.devground.dbay.cart.domain.port.in.CartUseCase;
import io.devground.dbay.cart.domain.vo.CartDescription;
import io.devground.dbay.cart.domain.vo.CartItemInfo;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartApplication implements CartUseCase {

    private final CartPersistencePort cartPersistencePort;
    private final CartProductPort cartProductPort;

    @Override
    @Transactional
    public Cart create(UserCode userCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        return cartPersistencePort
                .getCart(userCode)
                .orElseGet(() -> cartPersistencePort.saveCart(Cart.create(userCode)));
    }

    @Override
    @Transactional
    public CartItem addCartItem(UserCode userCode, ProductCode productCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        if (productCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        ProductSnapShot product = cartProductPort.getProduct(productCode);

        if  (product == null) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if ("SOLD".equals(product.productStatus())) {
            throw ServiceError.SOLD_PRODUCT_CANNOT_PURCHASE.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        return cartPersistencePort.saveCartItem(cart.getCartCode(), productCode);
    }

    @Override
    @Transactional
    public void removeCartItem(UserCode userCode, ProductCode productCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }
        if (productCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        cartPersistencePort.removeCartItem(cart.getCartCode(), productCode);
    }

    @Override
    @Transactional
    public void removeCartItems(UserCode userCode, List<ProductCode> productCodes) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }
        if (productCodes == null || productCodes.isEmpty()) {
            throw ServiceError.CART_ITEM_DELETE_NOT_SELECTED.throwServiceException();
        }

        if (productCodes.stream().anyMatch(Objects::isNull)) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        cartPersistencePort.removeCartItems(cart.getCartCode(), productCodes);
    }

    @Override
    @Transactional
    public void removeAllCartItems(UserCode userCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        cartPersistencePort.removeAllCartItems(cart.getCartCode());
    }

    @Override
    public Cart getCart(UserCode userCode) {
        return null;
    }

    @Override
    @Transactional
    public void deleteCart(UserCode userCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        cartPersistencePort.removeCart(userCode);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDescription getCartInfos(UserCode userCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        List<ProductCode> productCodes = cart.getCartItems().stream()
                .map(CartItem::getProductCode)
                .toList();

        List<ProductInfoSnapShot> productInfoSnapShots = cartProductPort.getCartProducts(productCodes);

        List<CartItemInfo> cartItemInfos = productInfoSnapShots.stream()
                .map(pi -> new CartItemInfo(pi.productCode() ,pi.title(), pi.price()))
                .toList();

        long totalAmount = cartItemInfos.stream().mapToLong(CartItemInfo::productPrice).sum();

        return new CartDescription(
                cart.getCartCode(),
                cartItemInfos,
                totalAmount
        );
    }
}
