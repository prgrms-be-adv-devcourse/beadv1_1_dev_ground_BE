package io.devground.dbay.domain.order.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.domain.order.order.model.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Page<Order> findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(String userCode, DeleteStatus deleteStatus,
		Pageable pageable);

	Page<Order> findByDeleteStatusOrderByCreatedAtDesc(DeleteStatus deleteStatus, Pageable pageable);

	Optional<Order> findByCodeAndUserCode(String orderCode, String userCode);

	Optional<Order> findByCode(String orderCode);

	@Modifying
	@Query("""
		UPDATE Order o
		SET o.deleteStatus = io.devground.core.model.vo.DeleteStatus.Y,
		o.updatedAt = TIMESTAMP
		WHERE o.code IN :orderCodes
		""")
	void DeleteByOrderCodes(List<String> orderCodes);

	@Query("""
		    SELECT o.id
		    FROM Order o
		    WHERE o.orderStatus = io.devground.dbay.domain.order.order.model.vo.OrderStatus.PAID
		      AND o.updatedAt <= :oneDayAgo
		""")
	List<Long> findOrdersToStartDelivery(@Param("oneDayAgo") LocalDateTime oneDayAgo);

	@Query("""
		    SELECT o.id
		    FROM Order o
		    WHERE o.orderStatus = io.devground.dbay.domain.order.order.model.vo.OrderStatus.DELIVERED
		      AND o.updatedAt <= :threeDaysAgo
		""")
	List<Long> findOrdersToCompleteDelivery(@Param("threeDaysAgo") LocalDateTime threeDaysAgo);

	@Modifying
	@Query("""
		    UPDATE Order o
		    SET o.orderStatus = io.devground.dbay.domain.order.order.model.vo.OrderStatus.START_DELIVERY
		    WHERE o.id IN :ids
		""")
	int changePaidToDelivery(@Param("ids") List<Long> ids);

	@Modifying
	@Query("""
		    UPDATE Order o
		    SET o.orderStatus = io.devground.dbay.domain.order.order.model.vo.OrderStatus.DELIVERED
		    WHERE o.id IN :ids
		""")
	int changeDeliveryToDelivered(@Param("ids") List<Long> ids);

}
