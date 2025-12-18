package io.devground.payments.deposit.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.payments.deposit.infrastructure.model.persistence.DepositHistoryEntity;

public interface DepositHistoryJpaRepository extends JpaRepository<DepositHistoryEntity, Long> {

	Page<DepositHistoryEntity> findByUserCode(String userCode, Pageable pageable);

	Optional<DepositHistoryEntity> findByCode(String historyCode);
}
