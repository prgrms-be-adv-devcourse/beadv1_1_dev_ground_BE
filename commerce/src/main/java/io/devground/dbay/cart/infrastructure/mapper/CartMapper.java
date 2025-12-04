package io.devground.dbay.cart.infrastructure.mapper;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;
import io.devground.dbay.cart.application.vo.ProductSnapShot;

import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.model.CartItem;
import io.devground.dbay.cart.domain.vo.CartCode;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;
import io.devground.dbay.cart.infrastructure.adapter.in.vo.AddCartItemResponse;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.CartProductsResponse;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.ProductDetailResponse;
import io.devground.dbay.cart.infrastructure.model.persistence.CartEntity;
import io.devground.dbay.cart.infrastructure.model.persistence.CartItemEntity;

import java.util.List;

public class CartMapper {
    public static Cart toCartDomain(CartEntity cartEntity) {
        return Cart.restore(
                new CartCode(cartEntity.getCode()),
                new UserCode(cartEntity.getUserCode()),
                cartEntity.getCartItems().stream()
                        .filter(ci -> ci.getDeleteStatus() == DeleteStatus.N)
                        .map(CartMapper::toCartItemDomain)
                        .toList()
        );
    }

    public static CartItem toCartItemDomain(CartItemEntity cartItemEntity) {
        return CartItem.of(
                new ProductCode(cartItemEntity.getProductCode()),
                new CartCode(cartItemEntity.getCartEntity().getCode())
        );
    }

    public static ProductSnapShot toProductSnapShot(ProductDetailResponse productDetailResponse) {
        return new ProductSnapShot(
                productDetailResponse.productCode(),
                productDetailResponse.productStatus()
        );
    }

    public static List<ProductInfoSnapShot> toProductInfosSnapShot(List<CartProductsResponse>  cartProductsResponses) {
        return cartProductsResponses.stream().map(c ->
            new ProductInfoSnapShot(
               new ProductCode(c.productCode()),
               c.productSaleCode(),
               c.sellerCode(),
               c.title(),
               c.price()
            )
        ).toList();
    }

    public static AddCartItemResponse toAddCartItemResponse(CartItem cartItem) {
        return new AddCartItemResponse(
                cartItem.getCartCode().value(),
                cartItem.getProductCode().value()
        );
    }
}
