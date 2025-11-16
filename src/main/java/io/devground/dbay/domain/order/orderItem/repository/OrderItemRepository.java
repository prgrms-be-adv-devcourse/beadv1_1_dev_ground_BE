package io.devground.dbay.domain.order.orderItem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	List<OrderItem> findByOrderInAndDeleteStatus(List<Order> orders, DeleteStatus deleteStatus);
	List<OrderItem> findByOrder(Order order);
}
