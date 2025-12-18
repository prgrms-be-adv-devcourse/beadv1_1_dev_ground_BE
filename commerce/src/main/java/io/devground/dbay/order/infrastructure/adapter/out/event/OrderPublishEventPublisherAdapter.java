package io.devground.dbay.order.infrastructure.adapter.out.event;

import io.devground.core.commands.payment.DepositRefundCommand;
import io.devground.core.event.order.OrderCreatedEvent;
import io.devground.dbay.order.application.port.out.event.OrderPublishEventPort;
import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.UserCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderPublishEventPublisherAdapter implements OrderPublishEventPort {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishEvent(UserCode userCode, OrderCode orderCode, long totalAmount, List<String> productCodes) {
        publisher.publishEvent(new OrderCreatedEvent(userCode.value(), orderCode.value(), totalAmount, productCodes));
    }

    @Override
    public void publishRefundEvent(UserCode userCode, Long amount, OrderCode orderCode) {
        publisher.publishEvent(new DepositRefundCommand(userCode.value(), amount, orderCode.value()));
    }
}
