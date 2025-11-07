package io.devground.dbay.domain.order.orderItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
