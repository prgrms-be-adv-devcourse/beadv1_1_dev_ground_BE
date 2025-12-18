package io.devground.dbay.cart.infrastructure.adapter.out.persistence;

import io.devground.dbay.cart.infrastructure.model.persistence.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByCode(String cartCode);

    Optional<CartEntity> findByUserCode(String userCode);
}
