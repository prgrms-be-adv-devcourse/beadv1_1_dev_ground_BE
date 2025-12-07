package io.devground.dbay.order.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;

import io.devground.dbay.order.domain.vo.OrderStatus;
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
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String userCode;

    @Column(columnDefinition = "VARCHAR(100)")
    private String nickName;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String address;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String addressDetail;

    @Column(nullable = false)
    private Long totalAmount;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToMany(mappedBy = "orderEntity")
    List<OrderItemEntity> orderItems = new ArrayList<>();

    @Builder
    public OrderEntity(String orderCode, String userCode, String nickName, String address, String addressDetail, Long totalAmount) {
        if (!StringUtils.hasText(userCode)) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (!StringUtils.hasText(nickName)) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (!StringUtils.hasText(address)) {
            throw ErrorCode.ADDRESS_NOT_FOUND.throwServiceException();
        }
        this.register(orderCode);
        this.userCode = userCode;
        this.nickName = nickName.trim();
        this.address = address;
        this.addressDetail = addressDetail;
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

