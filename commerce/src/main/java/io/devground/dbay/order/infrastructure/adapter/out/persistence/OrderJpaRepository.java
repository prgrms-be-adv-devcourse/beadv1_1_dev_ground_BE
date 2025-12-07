package io.devground.dbay.order.infrastructure.adapter.out.persistence;

import io.devground.dbay.order.infrastructure.model.persistence.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByCode(String orderCode);
}
