package io.devground.dbay.cart.infrastructure.adapter.out.product;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.cart.application.port.out.product.CartProductPort;
import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;
import io.devground.dbay.cart.application.vo.ProductSnapShot;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.CartProductsRequest;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.CartProductsResponse;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.ProductDetailResponse;
import io.devground.dbay.cart.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CartProductFeignAdapter implements CartProductPort {
    private final ProductFeignClient productFeignClient;

    @Override
    public ProductSnapShot getProduct(UserCode userCode, ProductCode productCode) {

        if (productCode == null) {
            throw new ServiceException(ErrorCode.CODE_INVALID);
        }

        ProductDetailResponse productDetail = productFeignClient.getProductDetail(userCode.value(), productCode.value())
                .throwIfNotSuccess().data();

        if (productDetail == null) {
            throw new ServiceException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return CartMapper.toProductSnapShot(productDetail);
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

        return CartMapper.toProductInfosSnapShot(cartProducts);
    }

}
