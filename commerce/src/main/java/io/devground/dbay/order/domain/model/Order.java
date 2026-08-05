package io.devground.dbay.order.domain.model;

import io.devground.dbay.order.domain.exception.DomainError;
import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.OrderProduct;
import io.devground.dbay.order.domain.vo.OrderStatus;
import io.devground.dbay.order.domain.vo.UserCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {
    private final OrderCode orderCode;

    private final UserCode userCode;

    private OrderStatus orderStatus;

    private final List<OrderItem> orderItems;

    private void validate(OrderCode orderCode, UserCode userCode) {
        if (orderCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (userCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }
    }

    private Order(OrderCode orderCode, UserCode userCode, OrderStatus orderStatus, List<OrderItem> orderItems) {
        validate(orderCode, userCode);

        if (orderStatus == null || orderItems == null) {
            throw DomainError.ORDER_NOT_FOUND.throwDomainException();
        }

        this.orderCode = orderCode;
        this.userCode = userCode;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }

    // 단건 생성
    public static Order createOne(UserCode userCode, OrderProduct orderProduct) {
        if (userCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (orderProduct == null) {
            throw DomainError.ORDER_ITEM_NOT_SELECTED.throwDomainException();
        }

        OrderCode orderCode = OrderCode.create();

        return new Order(
                orderCode,
                userCode,
                OrderStatus.PENDING,
                List.of(OrderItem.create(orderCode, orderProduct))
        );
    }

    // 선택 생성
    public static Order createSelected(UserCode userCode, List<OrderProduct> orderProducts) {
        if (userCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (orderProducts == null) {
            throw DomainError.ORDER_ITEM_NOT_SELECTED.throwDomainException();
        }

        if (orderProducts.stream().anyMatch(Objects::isNull)) {
            throw DomainError.ORDER_ITEM_NOT_SELECTED.throwDomainException();
        }

        OrderCode orderCode = OrderCode.create();

        List<OrderItem> orderItems = orderProducts.stream()
                .map(op -> OrderItem.create(orderCode, op))
                .toList();

        return new Order(orderCode, userCode, OrderStatus.PENDING, orderItems);
    }

    public static Order restore(
            OrderCode orderCode,
            UserCode userCode,
            OrderStatus orderStatus,
            List<OrderItem> orderItems
    ) {
        return new Order(orderCode, userCode, orderStatus, orderItems);
    }

    // 주문이 가지는 행동
    // 주문 상태 변경
    public void cancel() {
        if (this.orderStatus == OrderStatus.CANCELLED) {
            throw DomainError.ORDER_ALREADY_CANCELLED.throwDomainException();
        }

        if (this.orderStatus == OrderStatus.DELIVERED) {
            throw DomainError.ORDER_ALREADY_DELIVERED.throwDomainException();
        }

        if (this.orderStatus == OrderStatus.CONFIRMED) {
            throw DomainError.ORDER_ALREADY_CONFIRMED.throwDomainException();
        }

        this.orderStatus = OrderStatus.CANCELLED;
    }

    public void confirm(LocalDateTime deliveredAt, LocalDateTime confirmedAt) {
        if (this.orderStatus == OrderStatus.CONFIRMED) {
            throw DomainError.ORDER_ALREADY_CONFIRMED.throwDomainException();
        }

        if (this.orderStatus != OrderStatus.DELIVERED) {
            throw DomainError.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED.throwDomainException();
        }

        if (deliveredAt == null || confirmedAt == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (confirmedAt.isAfter(deliveredAt.plusWeeks(2))) {
            throw DomainError.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_TWO_WEEKS.throwDomainException();
        }

        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void paid() {
        this.orderStatus = OrderStatus.PAID;
    }

    public long totalPrice(List<OrderItem> orderItems) {
        return orderItems.stream().mapToLong(OrderItem::getProductPrice).sum();
    }

    public OrderCode getOrderCode() {
        return orderCode;
    }

    public UserCode getUserCode() {
        return userCode;
    }

    public List<OrderItem> getOrderItems() {
        return List.copyOf(orderItems);
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
