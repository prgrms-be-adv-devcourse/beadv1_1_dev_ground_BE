package io.devground.dbay.order.application.port.out.product;

import io.devground.dbay.order.application.vo.ProductInfoSnapShot;
import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.domain.vo.ProductCode;

import java.util.List;

public interface OrderProductPort {
    ProductSnapShot getProduct(ProductCode productCode);

    List<ProductInfoSnapShot> getCartProducts(List<ProductCode> productCodes);
}
