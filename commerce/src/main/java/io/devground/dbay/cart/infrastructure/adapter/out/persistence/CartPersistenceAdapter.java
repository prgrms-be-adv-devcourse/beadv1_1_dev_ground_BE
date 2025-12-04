package io.devground.dbay.cart.infrastructure.adapter.out.persistence;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.cart.application.port.out.persistence.CartPersistencePort;
import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.model.CartItem;
import io.devground.dbay.cart.domain.vo.CartCode;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;
import io.devground.dbay.cart.infrastructure.mapper.CartMapper;
import io.devground.dbay.cart.infrastructure.model.persistence.CartEntity;
import io.devground.dbay.cart.infrastructure.model.persistence.CartItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartPersistenceAdapter implements CartPersistencePort {

    private final CartJpaRepository cartJpaRepository;
    private final CartItemJpaRepository cartItemJpaRepository;


    @Override
    public Cart saveCart(Cart cart) {
        CartEntity cartEntity = cartJpaRepository.findByCode(cart.getCartCode().value())
                .orElseGet(() -> cartJpaRepository.save(
                        CartEntity.builder()
                                .cartCode(cart.getCartCode().value())
                                .userCode(cart.getUserCode().toString())
                                .build()
                ));

        return CartMapper.toCartDomain(cartEntity);
    }

    @Override
    public CartItem saveCartItem(CartCode cartCode, ProductCode productCode) {
        CartEntity cartEntity = cartJpaRepository.findByCode(cartCode.value())
                .orElseThrow(() -> new ServiceException(ErrorCode.CART_NOT_FOUND));

        CartItemEntity cartItemEntity = CartItemEntity.builder()
                .cartEntity(cartEntity)
                .productCode(productCode.value())
                .build();

        cartItemJpaRepository.save(cartItemEntity);

        return CartMapper.toCartItemDomain(cartItemEntity);
    }

    @Override
    public Optional<Cart> getCart(UserCode userCode) {
        return cartJpaRepository.findByUserCode(userCode.value())
                .map(CartMapper::toCartDomain);
    }

    @Override
    public void removeCartItem(CartCode cartCode, ProductCode productCode) {
        CartEntity cartEntity = getCartByCartCodeOrThrow(cartCode);
        cartItemJpaRepository.deleteCartItemEntityByProductCodes(cartEntity, List.of(productCode.value()));
    }

    @Override
    public void removeCartItems(CartCode cartCode, List<ProductCode> productCodes) {
        CartEntity cartEntity = getCartByCartCodeOrThrow(cartCode);

        cartItemJpaRepository.deleteCartItemEntityByProductCodes(
                cartEntity,
                productCodes.stream().map(ProductCode::value).toList()
        );
    }

    @Override
    public void removeAllCartItems(CartCode cartCode) {
        CartEntity cartEntity = getCartByCartCodeOrThrow(cartCode);
        cartItemJpaRepository.deleteCartItemEntityByCartCode(cartEntity);
    }

    @Override
    public void removeCart(UserCode userCode) {
        CartEntity cartEntity = getCartByUserCodeOrThrow(userCode);
        removeAllCartItems(new CartCode(cartEntity.getCode()));
        cartEntity.delete();
    }

    private CartEntity getCartByUserCodeOrThrow(UserCode userCode) {
        if (userCode == null) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUNT);
        }

        return cartJpaRepository.findByUserCode(userCode.value())
                .orElseThrow(() -> new ServiceException(ErrorCode.CART_NOT_FOUND));
    }

    private CartEntity getCartByCartCodeOrThrow(CartCode cartCode) {
        if (cartCode == null) {
            throw new ServiceException(ErrorCode.CART_NOT_FOUND);
        }

        return cartJpaRepository.findByCode(cartCode.value())
                .orElseThrow(() -> new ServiceException(ErrorCode.CART_NOT_FOUND));
    }
}
