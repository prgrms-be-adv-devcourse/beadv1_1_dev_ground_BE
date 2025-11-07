package io.devground.dbay.domain.order.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.order.order.model.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
