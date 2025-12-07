package io.devground.dbay.order.infrastructure.mapper;

import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.model.Order;
import io.devground.dbay.order.domain.model.OrderItem;
import io.devground.dbay.order.domain.vo.*;
import io.devground.dbay.order.infrastructure.model.persistence.OrderEntity;
import io.devground.dbay.order.infrastructure.model.persistence.OrderItemEntity;
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

    public static OrderDescription toOrderDescription(OrderEntity orderEntity) {
        return new OrderDescription(
                orderEntity.getCode(),
                orderEntity.getUserCode(),
                orderEntity.getCreatedAt(),
                orderEntity.getUpdatedAt(),
                orderEntity.getTotalAmount(),
                orderEntity.getOrderStatus(),
                orderEntity.getOrderItems().stream()
                        .map(OrderMapper::toOrderItemInfo)
                        .toList()
        );
    }

    public static OrderItemInfo toOrderItemInfo(OrderItemEntity orderItemEntity) {
        return new OrderItemInfo(
                orderItemEntity.getOrderEntity().getCode(),
                orderItemEntity.getProductName(),
                orderItemEntity.getProductPrice()
        );
    }
}
