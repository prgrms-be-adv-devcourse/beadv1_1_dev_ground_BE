package io.devground.dbay.order.domain.model;

import io.devground.dbay.cart.domain.exception.DomainError;
import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.OrderProduct;

public class OrderItem {

    private final OrderCode orderCode;

    private final OrderProduct orderProduct;

    private OrderItem(OrderCode orderCode, OrderProduct orderProduct) {
        if (orderCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        this.orderCode = orderCode;
        this.orderProduct = orderProduct;
    }

    public static OrderItem create(OrderCode orderCode, OrderProduct orderProduct) {
        return new OrderItem(orderCode, orderProduct);
    }

    public OrderCode getOrderCode() {
        return orderCode;
    }

    public String getProductCode() {
        return this.orderProduct.productCode();
    }

    public long getProductPrice() {
        return this.orderProduct.productPrice();
    }

    public String getProductName() {
        return this.orderProduct.productName();
    }

}
