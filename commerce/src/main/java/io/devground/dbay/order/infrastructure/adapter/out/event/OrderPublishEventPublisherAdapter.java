package io.devground.dbay.order.infrastructure.adapter.out.event;

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
    public void publishEvent(OrderCode orderCode, UserCode userCode, long totalAmount, List<String> productCodes) {
        publisher.publishEvent(new OrderCreatedEvent(orderCode.value(), userCode.value(), totalAmount, productCodes));
    }
}
