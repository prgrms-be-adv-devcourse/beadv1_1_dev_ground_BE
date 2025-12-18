package io.devground.dbay.order.application.port.out.product;

import io.devground.dbay.order.application.vo.ProductInfoSnapShot;
import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.domain.vo.ProductCode;
import io.devground.dbay.order.domain.vo.UserCode;

import java.util.List;

public interface OrderProductPort {
    ProductSnapShot getProduct(UserCode userCode, ProductCode productCode);

    List<ProductInfoSnapShot> getCartProducts(List<ProductCode> productCodes);
}
