package io.devground.dbay.order.application.port.out.persistence;

import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.model.Order;
import io.devground.dbay.order.domain.vo.OrderProduct;

import java.util.List;

public interface OrderPersistencePort {
    void createSingleOrder(UserInfo userInfo, Order order, OrderProduct orderProduct);
    void createSelectedOrder(UserInfo userInfo, Order order, List<OrderProduct> orderProducts);
}
