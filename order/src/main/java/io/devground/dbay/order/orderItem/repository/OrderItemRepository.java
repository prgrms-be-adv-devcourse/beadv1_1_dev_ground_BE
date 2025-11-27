package io.devground.dbay.order.orderItem.repository;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.order.order.model.entity.Order;
import io.devground.dbay.order.order.model.vo.UnsettledOrderItemResponse;
import io.devground.dbay.order.orderItem.model.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	List<OrderItem> findByOrderInAndDeleteStatus(List<Order> orders, DeleteStatus deleteStatus);

	List<OrderItem> findByOrder(Order order);

	@Query("""
		    SELECT new io.devground.dbay.order.order.model.vo.UnsettledOrderItemResponse(
		        o.code,
		        o.userCode,
		        oi.code,
		        oi.sellerCode,
		        oi.productPrice
		    )
		    FROM OrderItem oi
		    JOIN oi.order o
		    WHERE o.orderStatus = io.devground.dbay.order.order.model.vo.OrderStatus.DELIVERED
		      AND o.updatedAt BETWEEN :start AND :end
		""")
	Page<UnsettledOrderItemResponse> findOrderItemsDelivered(@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end,
		Pageable pageable);
}
