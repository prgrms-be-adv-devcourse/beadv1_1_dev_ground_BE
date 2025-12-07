package io.devground.dbay.order.application.port.out.persistence;

import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.model.Order;
import io.devground.dbay.order.domain.model.OrderItem;
import io.devground.dbay.order.domain.vo.*;
import io.devground.dbay.order.domain.vo.pagination.PageDto;
import io.devground.dbay.order.domain.vo.pagination.PageQuery;

import java.util.List;

public interface OrderPersistencePort {
    void createSingleOrder(UserInfo userInfo, Order order, OrderProduct orderProduct);
    void createSelectedOrder(UserInfo userInfo, Order order, List<OrderProduct> orderProducts);
    PageDto<OrderDescription> getOrders(UserCode userCode, RoleType roleType, PageQuery pageQuery);
    List<OrderItemInfo> getOrderItems(List<String> orderCodes);
    OrderDetailDescription getOrderDetail(UserCode userCode, OrderCode orderCode);
}
