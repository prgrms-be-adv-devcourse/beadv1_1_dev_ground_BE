package io.devground.dbay.ddddeposit.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.ddddeposit.infrastructure.model.persistence.DepositEntity;

public interface DepositJpaRepository extends JpaRepository<DepositEntity, Integer> {

	Optional<DepositEntity> findByCode(String code);

	void deleteByCode(String code);

	Optional<DepositEntity> findByUserCode(String userCode);
}
