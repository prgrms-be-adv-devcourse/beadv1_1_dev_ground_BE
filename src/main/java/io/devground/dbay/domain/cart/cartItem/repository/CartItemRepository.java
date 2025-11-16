package io.devground.dbay.domain.cart.cartItem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	boolean existsByCartAndProductCode(Cart cart, String productCode);

	@Modifying
	@Query("""
		DELETE FROM CartItem ci
		WHERE ci.cart = :cart
			AND ci.deleteStatus = 'N'
			AND ci.productCode IN :cartProductCodes
		""")
	int deleteCartItemByProductCodes(@Param("cart") Cart cart,
		@Param("cartProductCodes") List<String> cartProductCodes);

	@Modifying
	@Query("""
		DELETE FROM CartItem ci
		WHERE ci.cart = :cart
		""")
	void deleteCartItemByCartCode(@Param("cart") Cart cart);

	List<CartItem> findByCart(Cart cart);
}
