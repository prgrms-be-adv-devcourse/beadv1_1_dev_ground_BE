package io.devground.dbay.order.application.port.out.event;

import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.UserCode;

import java.util.List;

public interface OrderPublishEventPort {
    void publishEvent(UserCode userCode, OrderCode orderCode, long totalAmount, List<String> productCodes);
    void publishRefundEvent(UserCode userCode, Long amount, OrderCode orderCode);
}
