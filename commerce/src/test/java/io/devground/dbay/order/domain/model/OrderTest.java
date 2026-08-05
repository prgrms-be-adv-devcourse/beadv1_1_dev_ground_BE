package io.devground.dbay.order.domain.model;

import io.devground.dbay.order.domain.exception.DomainError;
import io.devground.dbay.order.domain.exception.DomainException;
import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.OrderStatus;
import io.devground.dbay.order.domain.vo.UserCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void restorePreservesThePersistedStatus() {
        Order order = restore(OrderStatus.DELIVERED);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void confirmAllowsDeliveredOrdersBeforeTheTwoWeekDeadline() {
        Order order = restore(OrderStatus.DELIVERED);
        LocalDateTime deliveredAt = LocalDateTime.of(2026, 7, 1, 12, 0);

        order.confirm(deliveredAt, deliveredAt.plusDays(13));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void confirmRejectsRequestsAfterTheTwoWeekDeadline() {
        Order order = restore(OrderStatus.DELIVERED);
        LocalDateTime deliveredAt = LocalDateTime.of(2026, 7, 1, 12, 0);

        assertThatThrownBy(() -> order.confirm(deliveredAt, deliveredAt.plusWeeks(2).plusNanos(1)))
                .isInstanceOf(DomainException.class)
                .extracting(exception -> ((DomainException) exception).getErrorCode())
                .isEqualTo(DomainError.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_TWO_WEEKS);
    }

    @Test
    void confirmRejectsOrdersThatHaveNotBeenDelivered() {
        Order order = restore(OrderStatus.PAID);
        LocalDateTime now = LocalDateTime.of(2026, 7, 1, 12, 0);

        assertThatThrownBy(() -> order.confirm(now, now))
                .isInstanceOf(DomainException.class)
                .extracting(exception -> ((DomainException) exception).getErrorCode())
                .isEqualTo(DomainError.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED);
    }

    private Order restore(OrderStatus orderStatus) {
        return Order.restore(
                new OrderCode(UUID.randomUUID().toString()),
                new UserCode(UUID.randomUUID().toString()),
                orderStatus,
                List.of()
        );
    }
}
