package io.devground.dbay.order.application.service;

import io.devground.dbay.order.application.exception.ServiceError;
import io.devground.dbay.order.application.port.out.persistence.OrderPersistencePort;
import io.devground.dbay.order.application.port.out.product.OrderProductPort;
import io.devground.dbay.order.application.port.out.user.OrderUserPort;
import io.devground.dbay.order.application.vo.ProductInfoSnapShot;
import io.devground.dbay.order.application.vo.ProductSnapShot;
import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.model.Order;
import io.devground.dbay.order.domain.model.OrderItem;
import io.devground.dbay.order.domain.port.in.OrderUseCase;
import io.devground.dbay.order.domain.vo.*;
import io.devground.dbay.order.domain.vo.pagination.PageDto;
import io.devground.dbay.order.domain.vo.pagination.PageQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderApplication implements OrderUseCase {

    private final OrderUserPort orderUserPort;
    private final OrderProductPort orderProductPort;
    private final OrderPersistencePort orderPersistencePort;

    public OrderApplication(OrderUserPort orderUserPort, OrderProductPort orderProductPort, OrderPersistencePort orderPersistencePort) {
        this.orderUserPort = orderUserPort;
        this.orderProductPort = orderProductPort;
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    @Transactional
    public void createOrderByOne(UserCode userCode, ProductCode productCode) {
        if (productCode == null) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        // 1. 먼저 유저 정보 가져오고
        UserInfo userInfo = getUserInfoOrThrow(userCode);

        // 2. 상품 정보 가져와
        ProductSnapShot product = orderProductPort.getProduct(productCode);

        if (product == null) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if (product.productStatus() == ProductStatus.SOLD) {
            throw ServiceError.SOLD_PRODUCT_CANNOT_PURCHASE.throwServiceException();
        }

        OrderProduct orderProduct = new OrderProduct(
                product.productCode(),
                product.sellerCode(),
                product.productName(),
                product.productPrice()
        );

        Order order = Order.createOne(userCode, orderProduct);

        // 3. 주문 생성
        orderPersistencePort.createSingleOrder(userInfo, order, orderProduct);
    }

    @Override
    @Transactional
    public void createOrderBySelected(UserCode userCode, List<ProductCode> productCodes) {

        UserInfo userInfo = getUserInfoOrThrow(userCode);

        if (productCodes == null || productCodes.isEmpty()) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if (productCodes.stream().anyMatch(Objects::isNull)) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        List<ProductInfoSnapShot> selProducts = orderProductPort
                .getCartProducts(productCodes);

        if (selProducts == null || selProducts.isEmpty()) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        List<OrderProduct> orderProducts = selProducts.stream()
                .map(sp -> new OrderProduct(
                        sp.productCode().value(),
                        sp.sellerCode(),
                        sp.title(),
                        sp.price()
                )).toList();

        Order order = Order.createSelected(userCode, orderProducts);

        orderPersistencePort.createSelectedOrder(userInfo, order, orderProducts);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<OrderDescription> getOrderLists(UserCode userCode, RoleType roleType, PageQuery pageQuery) {
        if (userCode == null) {
            throw ServiceError.USER_NOT_FOUNT.throwServiceException();
        }

        PageDto<OrderDescription> orderPage = orderPersistencePort.getOrders(userCode, roleType, pageQuery);

        List<OrderDescription> orders = orderPage.items();

        if (orders.isEmpty()) {
            return new PageDto<>(
                    pageQuery.page(),
                    pageQuery.size(),
                    0,
                    0,
                    List.of()
            );
        }

        List<String> orderCodes = orders.stream().map(OrderDescription::code).toList();

        List<OrderItemInfo> orderItems = orderPersistencePort.getOrderItems(orderCodes);

        Map<String, List<OrderItemInfo>> itemsByOrderId = orderItems.stream()
                .collect(Collectors.groupingBy(
                        OrderItemInfo::code,
                        Collectors.toList()
                ));

        List<OrderDescription> orderDescriptions = orders.stream()
                .map(o -> new OrderDescription(
                        o.code(),
                        o.userCode(),
                        o.createdAt(),
                        o.updatedAt(),
                        o.totalAmount(),
                        o.orderStatus(),
                        itemsByOrderId.getOrDefault(o.code(), Collections.emptyList())
                )).toList();

        return new PageDto<>(
                orderPage.currentPageNumber(),
                orderPage.pageSize(),
                orderPage.totalPages(),
                orderPage.totalItems(),
                orderDescriptions
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDescription getOrderDetail(UserCode userCode, OrderCode orderCode) {
        if (userCode == null) {
            throw ServiceError.USER_NOT_FOUNT.throwServiceException();
        }

        if (orderCode == null) {
            throw ServiceError.ORDER_NOT_FOUND.throwServiceException();
        }

        return orderPersistencePort.getOrderDetail(userCode, orderCode);
    }

    @Override
    public void confirmOrder(UserCode userCode, OrderCode orderCode) {
        if (userCode == null) {
            throw ServiceError.USER_NOT_FOUNT.throwServiceException();
        }

        if (orderCode == null) {
            throw ServiceError.ORDER_NOT_FOUND.throwServiceException();
        }

        Order order = orderPersistencePort.getOrder(orderCode);
        LocalDateTime updatedAt = orderPersistencePort.getUpdatedAtByOrder(orderCode);

        order.confirm(updatedAt);

        orderPersistencePort.confirm(orderCode);
    }

    @Override
    @Transactional
    public void cancelOrder(UserCode userCode, OrderCode orderCode) {
        if (userCode == null) {
            throw ServiceError.USER_NOT_FOUNT.throwServiceException();
        }

        if (orderCode == null) {
            throw ServiceError.ORDER_NOT_FOUND.throwServiceException();
        }

        Order order = orderPersistencePort.getOrder(orderCode);

        order.cancel();

        orderPersistencePort.cancel(orderCode);
    }

    private UserInfo getUserInfoOrThrow(UserCode userCode) {
        if (userCode == null) {
            throw ServiceError.USER_NOT_FOUNT.throwServiceException();
        }

        UserInfo userInfo = orderUserPort.getUserInfo(userCode);

        if (userInfo == null) {
            throw ServiceError.USER_NOT_FOUNT.throwServiceException();
        }

        return userInfo;
    }
}
