package io.devground.dbay.domain.order.orderItem.model.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.order.order.model.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String productCode;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String userCode;

	@Column(nullable = false, columnDefinition = "VARCHAR(100)")
	private String productName;

	@Column(nullable = false)
	private Long productPrice;

	public OrderItem(Order order, String productCode, String userCode, String productName, Long productPrice) {
		if (order == null) {
			throw ErrorCode.ORDER_NOT_FOUND.throwServiceException();
		}

		if (productCode == null || productCode.isBlank()) {
			throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		if (userCode == null || userCode.isBlank()) {
			throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
		}

		if (productName == null || productName.isBlank()) {
			throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		if (productPrice == null) {
			throw ErrorCode.AMOUNT_MUST_BE_POSITIVE.throwServiceException();
		}

		this.order = order;
		this.productCode = productCode;
		this.userCode = userCode;
		this.productName = productName;
		this.productPrice = productPrice;
	}
}
