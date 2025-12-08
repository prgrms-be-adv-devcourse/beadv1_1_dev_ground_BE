package io.devground.dbay.order.infrastructure.adapter.out.persistence;

import io.devground.dbay.order.infrastructure.model.persistence.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByCode(String orderCode);
    @Query("""
        SELECT o.id
        FROM OrderEntity o
        WHERE o.code IN :orderCodes
        """)
    List<Long> findIdByOrderCodes(List<String> orderCodes);

    @Query("""
        SELECT o
        FROM OrderEntity o
        WHERE o.userCode = :userCode
        AND o.deleteStatus = io.devground.core.model.vo.DeleteStatus.N
        """)
    Page<OrderEntity> findByNotDeletedOrders(String userCode, Pageable pageable);

    @Query("""
        SELECT o
        FROM OrderEntity o
        WHERE o.deleteStatus = io.devground.core.model.vo.DeleteStatus.N
        """)
    Page<OrderEntity> findAllByNotDeletedOrders(Pageable pageable);

    @Query("""
        UPDATE OrderEntity o
        SET o.orderStatus = io.devground.dbay.order.domain.vo.OrderStatus.CANCELLED
        WHERE o.code = :orderCode
        """)
    void cancelByCode(String orderCode);

}
