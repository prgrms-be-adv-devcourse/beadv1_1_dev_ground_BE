package io.devground.dbay.domain.cart.cartItem.model.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cartId", nullable = false)
	private Cart cart;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String productCode;

	@Builder
	public CartItem(Cart cart, String productCode) {
		if (cart == null) {
			throw new ServiceException(ErrorCode.CART_NOT_FOUND);
		}

		if (productCode == null || productCode.isBlank()) {
			throw ErrorCode.CART_NOT_FOUND.throwServiceException();
		}

		this.cart = cart;
		this.productCode = productCode;
	}
}
