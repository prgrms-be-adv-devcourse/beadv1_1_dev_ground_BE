package io.devground.dbay.domain.cart.cart.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
