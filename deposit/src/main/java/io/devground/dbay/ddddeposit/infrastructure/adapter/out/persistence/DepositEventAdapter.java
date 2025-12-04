package io.devground.dbay.ddddeposit.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import io.devground.dbay.ddddeposit.application.port.out.DepositCommandPort;
import io.devground.dbay.ddddeposit.domain.deposit.Deposit;
import io.devground.dbay.ddddeposit.infrastructure.mapper.DepositMapper;
import io.devground.dbay.ddddeposit.infrastructure.model.persistence.DepositEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DepositEventAdapter implements DepositCommandPort {

	private final DepositJpaRepository depositJpaRepository;

	@Override
	public Deposit saveDeposit(Deposit deposit) {
		DepositEntity depositEntity = depositJpaRepository.findByCode(deposit.getCode())
			.orElseGet(() -> depositJpaRepository.save(DepositEntity.of(deposit.getCode(), deposit.getUserCode())));

		return DepositMapper.toDomain(depositEntity);
	}

	@Override
	public void removeDeposit(String code) {
		depositJpaRepository.deleteByCode(code);
	}

}
