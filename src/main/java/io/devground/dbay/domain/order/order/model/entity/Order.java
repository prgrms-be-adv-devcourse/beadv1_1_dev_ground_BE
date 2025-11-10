package io.devground.dbay.domain.order.order.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.order.order.model.vo.OrderStatus;
import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "Orders")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String userCode;

	@Column(columnDefinition = "VARCHAR(100)")
	private String nickName;

	@Column(nullable = false, columnDefinition = "VARCHAR(100)")
	private String address;

	@Column(nullable = false)
	private Long totalAmount;

	@Setter
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.PENDING;

	@OneToMany(mappedBy = "order")
	List<OrderItem> orderItems = new ArrayList<>();

	@Builder
	public Order(String userCode, String nickName, String address, Long totalAmount) {
		if (!StringUtils.hasText(userCode)) {
			throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
		}

		if (!StringUtils.hasText(nickName)) {
			throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
		}

		if (!StringUtils.hasText(address)) {
			throw ErrorCode.ADDRESS_NOT_FOUND.throwServiceException();
		}

		this.userCode = userCode;
		this.nickName = nickName.trim();
		this.address = address;
		this.totalAmount = totalAmount;
	}
}
