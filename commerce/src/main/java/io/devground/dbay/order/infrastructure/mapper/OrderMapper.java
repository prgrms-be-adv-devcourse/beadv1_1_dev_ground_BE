package io.devground.dbay.order.infrastructure.mapper;

import io.devground.dbay.order.application.vo.ProductInfoSnapShot;
import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.model.Order;
import io.devground.dbay.order.domain.model.OrderItem;
import io.devground.dbay.order.domain.vo.*;
import io.devground.dbay.order.infrastructure.model.persistence.OrderEntity;
import io.devground.dbay.order.infrastructure.model.persistence.OrderItemEntity;
import io.devground.dbay.order.infrastructure.vo.CartProductsResponse;
import io.devground.dbay.order.infrastructure.vo.ProductDetailResponse;
import io.devground.dbay.order.infrastructure.vo.UserResponse;

import java.util.List;

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

    public static OrderDetailDescription toOrderDetailDescription(OrderEntity orderEntity, long productTotalAmount) {
        return new OrderDetailDescription(
                orderEntity.getCode(),
                orderEntity.getCreatedAt(),
                orderEntity.getUpdatedAt(),
                orderEntity.getOrderStatus(),
                orderEntity.getTotalAmount(),
                orderEntity.getTotalAmount() - productTotalAmount,
                productTotalAmount,
                0,
                orderEntity.getNickName(),
                orderEntity.getAddress(),
                orderEntity.getAddressDetail(),
                orderEntity.getOrderStatus().isCancellable()
        );
    }

    public static Order toOrderDomain(OrderEntity orderEntity) {
        return Order.restore(
                new OrderCode(orderEntity.getCode()),
                new UserCode(orderEntity.getUserCode()),
                orderEntity.getOrderItems().stream()
                        .map(OrderMapper::toOrderItemDomain)
                        .toList()
        );
    }

    public static OrderItem toOrderItemDomain(OrderItemEntity orderItemEntity) {
        return OrderItem.create(
                new OrderCode(orderItemEntity.getOrderEntity().getCode()),
                new OrderProduct(
                        orderItemEntity.getProductCode(),
                        orderItemEntity.getSellerCode(),
                        orderItemEntity.getProductName(),
                        orderItemEntity.getProductPrice()
                )
        );
    }

    public static List<ProductInfoSnapShot> toProductInfosSnapShot(List<CartProductsResponse>  cartProductsResponses) {
        return cartProductsResponses.stream().map(c ->
                new ProductInfoSnapShot(
                        new ProductCode(c.productCode()),
                        c.productSaleCode(),
                        c.sellerCode(),
                        c.title(),
                        c.thumbnail(),
                        c.price(),
                        c.description(),
                        c.categoryName()
                )
        ).toList();
    }
}
