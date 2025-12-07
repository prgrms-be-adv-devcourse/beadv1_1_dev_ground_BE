package io.devground.dbay.order.domain.port.in;

import io.devground.dbay.order.domain.vo.OrderProduct;
import io.devground.dbay.order.domain.vo.ProductCode;
import io.devground.dbay.order.domain.vo.UserCode;

import java.util.List;

public interface OrderUseCase {
    void createOrderByOne(UserCode userCode, ProductCode productCode);
    void createOrderBySelected(UserCode userCode, List<ProductCode> productCodes);
}
