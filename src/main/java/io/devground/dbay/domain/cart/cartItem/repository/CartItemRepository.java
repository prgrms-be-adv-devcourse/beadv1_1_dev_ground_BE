package io.devground.dbay.domain.cart.cartItem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	boolean existsByCart_CodeAndProductCode(String cartCode, String productCode);

	@Modifying
	@Query("""
		UPDATE CartItem ci
		SET ci.deleteStatus = 'Y',
			ci.updatedAt = CURRENT_TIMESTAMP
		WHERE ci.cart = :cartCode
			AND ci.deleteStatus = 'N'
			AND ci.productCode IN :cartProductCodes
		""")
	int deleteCartItemByProductCodes(@Param("cartCode") String cartCode, @Param("cartProductCodes")List<String> cartProductCodes);

	String cart(Cart cart);
}
