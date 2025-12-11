package io.devground.dbay.order.domain.port.in;

import io.devground.dbay.order.domain.vo.*;
import io.devground.dbay.order.domain.vo.pagination.PageDto;
import io.devground.dbay.order.domain.vo.pagination.PageQuery;

import java.util.List;

public interface OrderUseCase {
    void createOrderByOne(UserCode userCode, ProductCode productCode);
    void createOrderBySelected(UserCode userCode, List<ProductCode> productCodes);
    PageDto<OrderDescription> getOrderLists(UserCode userCode, RoleType roleType, PageQuery pageQuery);
    OrderDetailDescription getOrderDetail(UserCode userCode, OrderCode orderCode);
    void confirmOrder(UserCode userCode, OrderCode orderCode);
    void cancelOrder(UserCode userCode, OrderCode orderCode);
    void paidOrder(UserCode userCode, OrderCode orderCode);
    PageDto<UnsettledOrderItemResponse> getUnsettledOrderItems(PageQuery pageQuery);
    Progress autoUpdateOrderStatus();
}
