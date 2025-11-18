package io.devground.dbay.domain.order.order.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
		LocalDate today = LocalDate.now().minusDays(1);
		LocalDate twoWeeksAgo = today.minusWeeks(2);

		LocalDateTime start = twoWeeksAgo.atStartOfDay();
		LocalDateTime end = today.atTime(LocalTime.MAX);

		if (this.orderStatus == OrderStatus.CONFIRMED) {
			throw ErrorCode.ORDER_ALREADY_CONFIRMED.throwServiceException();
		}

		if (this.orderStatus != OrderStatus.DELIVERED) {
			throw ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED.throwServiceException();
		}

		if (this.getUpdatedAt().isAfter(end)) {
			throw ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_TWO_WEEKS.throwServiceException();
		}

		this.orderStatus = OrderStatus.CONFIRMED;
	}

	public void paid() {
		this.orderStatus = OrderStatus.PAID;
	}
}
