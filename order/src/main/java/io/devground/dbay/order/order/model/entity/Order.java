package io.devground.dbay.order.order.model.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.order.order.model.vo.OrderStatus;
import io.devground.dbay.order.orderItem.model.entity.OrderItem;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	public void cancel() {
		if (this.orderStatus == OrderStatus.CANCELLED) {
			throw ErrorCode.ORDER_ALREADY_CANCELLED.throwServiceException();
		}

		if (this.orderStatus == OrderStatus.DELIVERED) {
			throw ErrorCode.ORDER_CANCELLED_NOT_ALLOWED_WHEN_DELIVERED.throwServiceException();
		}

		if (this.orderStatus == OrderStatus.CONFIRMED) {
			throw ErrorCode.ORDER_CANCELLED_NOT_ALLOWED_WHEN_CONFIRMED.throwServiceException();
		}

		this.orderStatus = OrderStatus.CANCELLED;
	}

	public void confirm() {
		if (this.orderStatus == OrderStatus.CONFIRMED) {
			throw ErrorCode.ORDER_ALREADY_CONFIRMED.throwServiceException();
		}

		if (this.orderStatus != OrderStatus.DELIVERED) {
			throw ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED.throwServiceException();
		}

		LocalDateTime deliveredAt = this.getUpdatedAt();
		LocalDateTime twoWeeksAfterDelivery = deliveredAt.plusWeeks(2);

		if (deliveredAt.isAfter(twoWeeksAfterDelivery)) {
			throw ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_TWO_WEEKS.throwServiceException();
		}

		this.orderStatus = OrderStatus.CONFIRMED;
	}

	public void paid() {
		this.orderStatus = OrderStatus.PAID;
	}
}
