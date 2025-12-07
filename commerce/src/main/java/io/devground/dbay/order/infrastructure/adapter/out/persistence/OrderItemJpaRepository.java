package io.devground.dbay.order.infrastructure.adapter.out.persistence;

import io.devground.dbay.order.infrastructure.model.persistence.OrderEntity;
import io.devground.dbay.order.infrastructure.model.persistence.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {

    @Query("""
        SELECT oi
        FROM OrderItemEntity oi
        WHERE oi.orderEntity IN :orderIds
        """)
    List<OrderItemEntity> findAllByOrderIds(List<Long> orderIds);

}
