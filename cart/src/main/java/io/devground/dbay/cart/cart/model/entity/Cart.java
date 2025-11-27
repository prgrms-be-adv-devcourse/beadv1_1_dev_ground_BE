package io.devground.dbay.cart.cart.model.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.cart.cartItem.model.entity.CartItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String userCode;

	@OneToMany(mappedBy = "cart")
	List<CartItem> cartItems = new ArrayList<>();

	@Builder
	public Cart(String userCode) {
		if (!StringUtils.hasText(userCode)) {
			throw ErrorCode.CART_NOT_FOUND.throwServiceException();
		}
		this.userCode = userCode;
	}
}
