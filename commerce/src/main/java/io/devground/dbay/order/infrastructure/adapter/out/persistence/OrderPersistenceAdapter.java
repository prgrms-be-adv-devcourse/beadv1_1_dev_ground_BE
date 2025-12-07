package io.devground.dbay.order.infrastructure.adapter.out.persistence;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.order.application.port.out.persistence.OrderPersistencePort;
import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.model.Order;
import io.devground.dbay.order.domain.vo.OrderProduct;
import io.devground.dbay.order.infrastructure.model.persistence.OrderEntity;
import io.devground.dbay.order.infrastructure.model.persistence.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPersistencePort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public void createSingleOrder(UserInfo userInfo, Order order, OrderProduct orderProduct) {

        if (userInfo == null) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (order == null || orderProduct == null) {
            throw ErrorCode.ORDER_NOT_FOUND.throwServiceException();
        }

        OrderEntity orderEntity = OrderEntity.builder()
                .orderCode(order.getOrderCode().value())
                .userCode(order.getUserCode().value())
                .nickName(userInfo.nickname())
                .address(userInfo.address())
                .addressDetail(userInfo.addressDetail())
                .totalAmount(order.totalPrice(order.getOrderItems()))
                .build();

        orderJpaRepository.save(orderEntity);

        OrderItemEntity orderItemEntity = OrderItemEntity.builder()
                .orderEntity(orderEntity)
                .productCode(orderProduct.productCode())
                .sellerCode(orderProduct.sellerCode())
                .productName(orderProduct.productName())
                .productPrice(orderProduct.productPrice())
                .build();

        orderItemJpaRepository.save(orderItemEntity);
    }

    @Override
    public void createSelectedOrder(UserInfo userInfo, Order order, List<OrderProduct> orderProducts) {
        if (userInfo == null) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (order == null || orderProducts == null) {
            throw ErrorCode.ORDER_NOT_FOUND.throwServiceException();
        }

        if (orderProducts.stream().anyMatch(Objects::isNull)) {
            throw ErrorCode.ORDER_NOT_FOUND.throwServiceException();
        }

        OrderEntity orderEntity = OrderEntity.builder()
                .orderCode(order.getOrderCode().value())
                .userCode(order.getUserCode().value())
                .nickName(userInfo.nickname())
                .address(userInfo.address())
                .addressDetail(userInfo.addressDetail())
                .totalAmount(order.totalPrice(order.getOrderItems()))
                .build();

        orderJpaRepository.save(orderEntity);

        List<OrderItemEntity> orderItems = orderProducts.stream()
                .map(op -> OrderItemEntity.builder()
                        .orderEntity(orderEntity)
                        .productCode(op.productCode())
                        .sellerCode(op.sellerCode())
                        .productName(op.productName())
                        .productPrice(op.productPrice())
                        .build()
                ).toList();

        orderItemJpaRepository.saveAll(orderItems);
    }
}
