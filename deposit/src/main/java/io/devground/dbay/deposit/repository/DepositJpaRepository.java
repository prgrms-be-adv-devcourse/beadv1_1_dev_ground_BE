package io.devground.dbay.deposit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.deposit.entity.Deposit;

public interface DepositJpaRepository extends JpaRepository<Deposit, Long> {

	Optional<Deposit> findByUserCode(String userCode);

	boolean existsByUserCode(String userCode);

	Optional<Deposit> findByCode(String code);

	void deleteByUserCode(String userCode);
}
