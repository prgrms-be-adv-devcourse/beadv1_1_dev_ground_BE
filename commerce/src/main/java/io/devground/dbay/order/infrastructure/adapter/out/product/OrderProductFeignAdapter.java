package io.devground.dbay.order.infrastructure.adapter.out.product;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.order.infrastructure.vo.ProductDetailResponse;
import io.devground.dbay.order.application.port.out.product.OrderProductPort;
import io.devground.dbay.order.application.vo.ProductInfoSnapShot;
import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.domain.vo.ProductCode;
import io.devground.dbay.order.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderProductFeignAdapter implements OrderProductPort {

    private final ProductFeignClient productFeignClient;

    @Override
    public ProductSnapShot getProduct(ProductCode productCode) {
        if (productCode == null) {
            throw ErrorCode.CODE_INVALID.throwServiceException();
        }

        ProductDetailResponse productDetail = productFeignClient
                .getProductDetail(productCode.value())
                .throwIfNotSuccess().data();

        if (productDetail == null) {
            throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
        }

        return OrderMapper.toProductSnapShot(productDetail);
    }

    @Override
    public List<ProductInfoSnapShot> getCartProducts(List<ProductCode> productCodes) {
        return List.of();
    }
}
