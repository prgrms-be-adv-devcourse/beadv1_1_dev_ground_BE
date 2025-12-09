package io.devground.dbay.order.application.port.out.event;

import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.UserCode;

import java.util.List;

public interface OrderPublishEventPort {
    void publishEvent(OrderCode orderCode, UserCode userCode, long totalAmount, List<String> productCodes);
}
