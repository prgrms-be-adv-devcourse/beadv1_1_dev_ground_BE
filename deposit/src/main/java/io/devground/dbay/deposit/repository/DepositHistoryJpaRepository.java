package io.devground.dbay.deposit.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.deposit.entity.DepositHistory;

public interface DepositHistoryJpaRepository extends JpaRepository<DepositHistory, Long> {

	Page<DepositHistory> findByUserCode(String depositCode, Pageable pageable);

	Optional<DepositHistory> findByCode(String historyCode);
}
