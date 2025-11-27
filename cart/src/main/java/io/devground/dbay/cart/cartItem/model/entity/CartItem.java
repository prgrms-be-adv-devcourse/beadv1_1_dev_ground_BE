package io.devground.dbay.cart.cartItem.model.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.cart.cart.model.entity.Cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

		if (!StringUtils.hasText(productCode)) {
			throw ErrorCode.CART_NOT_FOUND.throwServiceException();
		}

		this.cart = cart;
		this.productCode = productCode;
	}
}
