package io.devground.dbay.order.infrastructure.adapter.out.product;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.cart.infrastructure.mapper.CartMapper;
import io.devground.dbay.order.domain.vo.UserCode;
import io.devground.dbay.order.infrastructure.vo.CartProductsRequest;
import io.devground.dbay.order.infrastructure.vo.CartProductsResponse;
import io.devground.dbay.order.infrastructure.vo.ProductDetailResponse;
import io.devground.dbay.order.application.port.out.product.OrderProductPort;
import io.devground.dbay.order.application.vo.ProductInfoSnapShot;
import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.domain.vo.ProductCode;
import io.devground.dbay.order.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderProductFeignAdapter implements OrderProductPort {

    private final ProductFeignClient productFeignClient;

    @Override
    public ProductSnapShot getProduct(UserCode userCode, ProductCode productCode) {
        if (productCode == null) {
            throw ErrorCode.CODE_INVALID.throwServiceException();
        }

        ProductDetailResponse productDetail = productFeignClient
                .getProductDetail(userCode.value(), productCode.value())
                .throwIfNotSuccess().data();

        if (productDetail == null) {
            throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
        }

        return OrderMapper.toProductSnapShot(productDetail);
    }

    @Override
    public List<ProductInfoSnapShot> getCartProducts(List<ProductCode> productCodes) {
        if (productCodes == null || productCodes.isEmpty()) {
            return List.of();
        }

        if (productCodes.stream().anyMatch(Objects::isNull)) {
            throw new ServiceException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        List<String> pCodes = productCodes.stream()
                .map(ProductCode::value)
                .toList();

        List<CartProductsResponse> cartProducts = productFeignClient.getCartProducts(new CartProductsRequest(pCodes))
                .throwIfNotSuccess().data();

        if (cartProducts == null || cartProducts.isEmpty()) {
            throw new ServiceException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return OrderMapper.toProductInfosSnapShot(cartProducts);
    }
}
