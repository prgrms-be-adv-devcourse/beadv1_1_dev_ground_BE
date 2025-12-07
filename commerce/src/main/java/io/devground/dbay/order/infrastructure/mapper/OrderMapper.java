package io.devground.dbay.order.infrastructure.mapper;

import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.infrastructure.vo.ProductDetailResponse;
import io.devground.dbay.order.infrastructure.vo.UserResponse;

public class OrderMapper {

    public static UserInfo toUserInfo(UserResponse userResponse) {
        return new UserInfo(
                userResponse.nickname(),
                userResponse.address(),
                userResponse.addressDetail()
        );
    }

    public static ProductSnapShot toProductSnapShot(ProductDetailResponse productDetailResponse) {
        return new ProductSnapShot(
                productDetailResponse.productCode(),
                productDetailResponse.sellerCode(),
                productDetailResponse.title(),
                productDetailResponse.price(),
                productDetailResponse.productStatus()
        );
    }
}
