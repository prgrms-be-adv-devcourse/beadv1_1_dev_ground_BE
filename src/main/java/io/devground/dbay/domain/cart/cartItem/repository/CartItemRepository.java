package io.devground.dbay.domain.cart.cartItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
