package io.devground.dbay.order.application.port.out.kafka;

import io.devground.dbay.order.domain.vo.DepositType;

import java.util.List;

public interface OrderKafkaEventPort {
    void publishOrderCreated(String userCode, String orderCode, long totalAmount, List<String> productCodes);
    void publishPaymentFailedToOrder(String userCode, String orderCode);
    void publishPaymentSuccessToDeposit(String userCode, long amount, DepositType type, String orderCode, List<String> productCodes);
    void publishDepositFailedToPayment(String userCode, String orderCode, String msg);
    void publishDepositFailedToOrder(String userCode, String orderCode, String msg);
    void publishDepositSuccessCompletePayment(String orderCode);
    void publishDepositSuccessCompleteOrder(String userCode, String orderCode);
    void publishDepositSuccessCompleteDeleteCart(String userCode, String orderCode, List<String> productCodes);
    void publishDepositRefundCreated(String userCode, Long amount, String orderCode);
    void publishDepositSuccessCompleteProduct(String orderCode, List<String> productCodes);
}
