package io.devground.dbay.domain.cart.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByCode(String cartCode);
	boolean existsByUserCode(String userCode);
	boolean existsByCode(String cartCode);
}
