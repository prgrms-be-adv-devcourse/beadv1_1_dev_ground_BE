package io.devground.dbay.domain.order.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
}
