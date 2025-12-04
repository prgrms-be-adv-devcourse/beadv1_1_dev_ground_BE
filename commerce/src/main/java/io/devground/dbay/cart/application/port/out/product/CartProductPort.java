package io.devground.dbay.cart.application.port.out.product;

import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;
import io.devground.dbay.cart.application.vo.ProductSnapShot;
import io.devground.dbay.cart.domain.vo.ProductCode;

import java.util.List;

public interface CartProductPort {
    ProductSnapShot getProduct(ProductCode productCode);

    List<ProductInfoSnapShot> getCartProducts(List<ProductCode> productCodes);
}
