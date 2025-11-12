package io.devground.dbay.domain.cart.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByCode(String cartCode);

	boolean existsByUserCode(String userCode);
}
