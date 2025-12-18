package io.devground.payments.deposit.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import io.devground.payments.deposit.application.port.out.DepositCommandPort;
import io.devground.payments.deposit.domain.deposit.Deposit;
import io.devground.payments.deposit.infrastructure.mapper.DepositMapper;
import io.devground.payments.deposit.infrastructure.model.persistence.DepositEntity;
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
			.map(d -> {
				d.updateBalance(deposit.getBalance());
				return d;
			})
			.orElseGet(() -> DepositEntity.from(deposit));

		DepositEntity saved = depositJpaRepository.save(depositEntity);
		return DepositMapper.toDomain(saved);
	}

	@Override
	public void removeDeposit(String code) {
		depositJpaRepository.deleteByCode(code);
	}

}
