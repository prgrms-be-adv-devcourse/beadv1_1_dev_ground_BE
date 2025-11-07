package io.devground.dbay.domain.deposit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.deposit.entity.Deposit;

public interface DepositHistoryJpaRepository extends JpaRepository<Deposit, Long> {

}
