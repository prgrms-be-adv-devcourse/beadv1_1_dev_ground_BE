package io.devground.dbay.cart.cart.repository;

import io.devground.dbay.cart.cart.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByCode(String cartCode);

	Optional<Cart> findByUserCode(String userCode);
}
