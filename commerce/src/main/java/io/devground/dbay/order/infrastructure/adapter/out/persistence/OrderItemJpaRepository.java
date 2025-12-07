package io.devground.dbay.order.infrastructure.adapter.out.persistence;

import io.devground.dbay.order.infrastructure.model.persistence.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
}
