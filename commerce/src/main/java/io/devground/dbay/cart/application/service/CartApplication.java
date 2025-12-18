package io.devground.dbay.cart.application.service;

import io.devground.dbay.cart.application.exception.ServiceError;
import io.devground.dbay.cart.application.port.out.ai.PromptPort;
import io.devground.dbay.cart.application.port.out.ai.VectorSearchPort;
import io.devground.dbay.cart.application.port.out.product.CartProductPort;
import io.devground.dbay.cart.application.port.out.persistence.CartPersistencePort;
import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;
import io.devground.dbay.cart.application.vo.ProductSnapShot;
import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.model.CartItem;
import io.devground.dbay.cart.domain.port.in.CartUseCase;
import io.devground.dbay.cart.domain.vo.*;

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
    private final PromptPort promptPort;
    private final VectorSearchPort vectorSearchPort;

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

        ProductSnapShot product = cartProductPort.getProduct(userCode, productCode);

        if  (product == null) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if ("SOLD".equals(product.productStatus())) {
            throw ServiceError.SOLD_PRODUCT_CANNOT_PURCHASE.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        if (cart.getCartItems().stream().anyMatch(p -> productCode.equals(p.getProductCode()))) {
            throw ServiceError.CART_ITEM_ALREADY_EXIST.throwServiceException();
        }

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
                .map(pi ->
                        new CartItemInfo(
                                pi.productCode().value(),
                                pi.thumbnail(),
                                pi.title(),
                                pi.price()
                        ))
                .toList();

        long totalAmount = cartItemInfos.stream().mapToLong(CartItemInfo::productPrice).sum();

        return new CartDescription(
                cart.getCartCode(),
                cartItemInfos,
                totalAmount
        );
    }

    @Override
    public List<CartItemInfo> recommendProductsByCartItem(UserCode userCode) {
        if (userCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        Cart cart = cartPersistencePort.getCart(userCode)
                .orElseThrow(ServiceError.CART_NOT_FOUND::throwServiceException);

        if (cart.getCartItems().size() < 3) {
            return List.of();
        }

        List<ProductCode> productCodes = cart.getCartItems().stream()
                .map(CartItem::getProductCode)
                .toList();

        List<ProductInfoSnapShot> productInfoSnapShots = cartProductPort.getCartProducts(productCodes);

        CartContext ctx = CartContext.of(productInfoSnapShots);

        String keywordQuery = promptPort.generateRecommendPrompt(ctx);

        List<CartRecommendVectorHits> cartRecommendVectorHits = vectorSearchPort.vectorSearch(keywordQuery, 20, 5);

        List<ProductCode> recommendProductCodes = cartRecommendVectorHits.stream()
                .map(crv -> new ProductCode(crv.productCode()))
                .toList();

        List<ProductInfoSnapShot> cartProducts = cartProductPort.getCartProducts(recommendProductCodes);

        return cartProducts.stream()
                .map(ci -> new CartItemInfo(
                        ci.productCode().value(),
                        ci.thumbnail(),
                        ci.title(),
                        ci.price()
                )).toList();
    }
}
