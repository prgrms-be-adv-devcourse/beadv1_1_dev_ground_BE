package io.devground.dbay.order.infrastructure.adapter.out.kafka;

import io.devground.core.commands.cart.DeleteCartItemsCommand;
import io.devground.core.commands.deposit.WithdrawDeposit;
import io.devground.core.commands.order.CompleteOrderCommand;
import io.devground.core.commands.order.NotifyOrderCreateFailedAlertCommand;
import io.devground.core.commands.payment.CancelCreatePaymentCommand;
import io.devground.core.commands.payment.CompletePaymentCommand;
import io.devground.core.commands.payment.PaymentCreateCommand;
import io.devground.dbay.order.application.port.out.kafka.OrderKafkaEventPort;
import io.devground.dbay.order.domain.vo.DepositType;
import io.devground.dbay.order.infrastructure.mapper.EnumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderKafkaEventPublisherAdapter implements OrderKafkaEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${orders.command.topic.purchase}")
    private String ordersCommandTopicName;

    @Value("${payments.command.topic.purchase}")
    private String paymentsCommandTopicName;

    @Value("${deposits.command.topic.purchase}")
    private String depositsCommandTopicName;

    @Value("${carts.command.topic.purchase}")
    private String cartsCommandTopicName;

    @Override
    public void publishOrderCreated(String userCode, String orderCode, long totalAmount, List<String> productCodes) {
        kafkaTemplate.send(paymentsCommandTopicName, orderCode, new PaymentCreateCommand(
                userCode,
                orderCode,
                totalAmount,
                productCodes
        ));
    }

    @Override
    public void publishPaymentFailedToOrder(String userCode, String orderCode) {
        kafkaTemplate.send(ordersCommandTopicName, orderCode, new NotifyOrderCreateFailedAlertCommand(
                userCode,
                orderCode,
                "결제 요청에 실패하였습니다."
        ));
    }

    @Override
    public void publishPaymentSuccessToDeposit(String userCode, long amount, DepositType type, String orderCode, List<String> productCodes) {
        kafkaTemplate.send(depositsCommandTopicName, orderCode, new WithdrawDeposit(
                userCode,
                amount,
                EnumMapper.toCoreDepositHistoryType(type),
                orderCode,
                productCodes
        ));
    }

    @Override
    public void publishDepositFailedToPayment(String userCode, String orderCode, String msg) {
        kafkaTemplate.send(paymentsCommandTopicName, orderCode, new CancelCreatePaymentCommand(
                userCode,
                orderCode,
                msg
        ));
    }

    @Override
    public void publishDepositFailedToOrder(String userCode, String orderCode, String msg) {
        kafkaTemplate.send(ordersCommandTopicName, orderCode, new NotifyOrderCreateFailedAlertCommand(
                userCode,
                orderCode,
                msg
        ));
    }

    @Override
    public void publishDepositSuccessCompletePayment(String orderCode) {
        kafkaTemplate.send(paymentsCommandTopicName, orderCode, new CompletePaymentCommand(orderCode));
    }

    @Override
    public void publishDepositSuccessCompleteOrder(String userCode, String orderCode) {
        kafkaTemplate.send(ordersCommandTopicName, orderCode, new CompleteOrderCommand(
                userCode,
                orderCode
        ));
    }

    @Override
    public void publishDepositSuccessCompleteDeleteCart(String userCode, String orderCode, List<String> productCodes) {
        kafkaTemplate.send(cartsCommandTopicName, orderCode, new DeleteCartItemsCommand(
                userCode,
                productCodes
        ));
    }
}
