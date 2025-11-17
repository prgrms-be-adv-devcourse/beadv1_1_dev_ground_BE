package io.devground.dbay.domain.order.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

	@Query(
		"""
				SELECT
				o
				FROM Order o
				WHERE o.orderStatus = io.devground.dbay.domain.order.order.model.vo.OrderStatus.DELIVERED
				AND o.updatedAt BETWEEN :start AND :end
			"""
	)
	Page<Order> findOrderBeforeConfirmed(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
		Pageable pageable);
}
