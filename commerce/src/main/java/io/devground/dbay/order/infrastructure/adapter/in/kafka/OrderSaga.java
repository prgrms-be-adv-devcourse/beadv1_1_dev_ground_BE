package io.devground.dbay.order.infrastructure.adapter.in.kafka;

import io.devground.core.event.deposit.DepositWithdrawFailed;
import io.devground.core.event.deposit.DepositWithdrawnSuccess;
import io.devground.core.event.order.OrderCreatedEvent;
import io.devground.core.event.payment.CancelCreatePaymentEvent;
import io.devground.core.event.payment.PaymentCreatedEvent;
import io.devground.core.event.payment.PaymentCreatedFailed;
import io.devground.dbay.order.application.port.out.kafka.OrderKafkaEventPort;
import io.devground.dbay.order.infrastructure.mapper.EnumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = {
                "${orders.event.topic.purchase}",
                "${payments.event.topic.purchase}",
                "${deposits.event.topic.purchase}"
        }
)
@RequiredArgsConstructor
public class OrderSaga {

    private final OrderKafkaEventPort orderEventPort;

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event) {
        orderEventPort.publishOrderCreated(event.userCode(), event.orderCode(), event.totalAmount(), event.productCodes());
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentCreatedFailed event) {
        orderEventPort.publishPaymentFailedToOrder(event.userCode(), event.orderCode());
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentCreatedEvent event) {
        orderEventPort.publishPaymentSuccessToDeposit(event.userCode(),
                event.amount(),
                EnumMapper.toDepositType(event.type()),
                event.orderCode(),
                event.productCodes()
        );
    }

    @KafkaHandler
    public void handleEvent(@Payload DepositWithdrawFailed event) {
        orderEventPort.publishDepositFailedToPayment(event.userCode(), event.orderCode(), event.msg());
    }

    @KafkaHandler
    public void handleEvent(@Payload CancelCreatePaymentEvent event) {
        orderEventPort.publishDepositFailedToOrder(event.userCode(), event.orderCode(), event.msg());
    }

    @KafkaHandler
    public void handleEvent(@Payload DepositWithdrawnSuccess event) {
        orderEventPort.publishDepositSuccessCompletePayment(event.orderCode());
        orderEventPort.publishDepositSuccessCompleteOrder(event.userCode(), event.orderCode());
        orderEventPort.publishDepositSuccessCompleteDeleteCart(event.userCode(), event.orderCode(), event.productCodes());
    }
}
