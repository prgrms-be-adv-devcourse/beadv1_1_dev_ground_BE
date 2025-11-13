package io.devground.dbay.domain.order.orderItem.model.entity;

import org.springframework.util.StringUtils;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "orderCode", nullable = false)
	private Order order;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String productCode;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String sellerCode;

	@Column(nullable = false, columnDefinition = "VARCHAR(100)")
	private String productName;

	@Column(nullable = false)
	private Long productPrice;

	@Builder
	public OrderItem(Order order, String productCode, String sellerCode, String productName, Long productPrice) {
		if (order == null) {
			throw ErrorCode.ORDER_NOT_FOUND.throwServiceException();
		}

		if (!StringUtils.hasText(productCode)) {
			throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		if (!StringUtils.hasText(sellerCode)) {
			throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
		}

		if (!StringUtils.hasText(productName)) {
			throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		if (productPrice == null) {
			throw ErrorCode.AMOUNT_MUST_BE_POSITIVE.throwServiceException();
		}

		this.order = order;
		this.productCode = productCode;
		this.sellerCode = sellerCode;
		this.productName = productName;
		this.productPrice = productPrice;
	}
}
