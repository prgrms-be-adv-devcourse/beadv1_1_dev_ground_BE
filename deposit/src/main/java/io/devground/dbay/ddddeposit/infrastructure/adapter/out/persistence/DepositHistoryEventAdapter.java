package io.devground.dbay.ddddeposit.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import io.devground.dbay.ddddeposit.application.exception.vo.ServiceErrorCode;
import io.devground.dbay.ddddeposit.application.port.out.DepositHistoryCommandPort;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistory;
import io.devground.dbay.ddddeposit.domain.pagination.PageDto;
import io.devground.dbay.ddddeposit.domain.pagination.PageQuery;
import io.devground.dbay.ddddeposit.infrastructure.mapper.DepositHistoryMapper;
import io.devground.dbay.ddddeposit.infrastructure.model.persistence.DepositEntity;
import io.devground.dbay.ddddeposit.infrastructure.model.persistence.DepositHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DepositHistoryEventAdapter implements DepositHistoryCommandPort {

	private final DepositHistoryJpaRepository depositHistoryJpaRepository;
	private final DepositJpaRepository depositJpaRepository;

	@Override
	public DepositHistory saveDepositHistory(DepositHistory depositHistory) {
		log.debug("Saving deposit history: {}", depositHistory.getCode());

		DepositEntity depositEntity = depositJpaRepository.findByCode(depositHistory.getDepositCode())
			.orElseThrow(() -> ServiceErrorCode.DEPOSIT_NOT_FOUND
				.throwServiceException("depositCode: " + depositHistory.getDepositCode()));

		DepositEntity payerDepositEntity = depositJpaRepository.findByCode(depositHistory.getPayerDepositCode())
			.orElseThrow(() -> ServiceErrorCode.DEPOSIT_NOT_FOUND
				.throwServiceException("depositCode: " + depositHistory.getPayerDepositCode()));

		DepositEntity payeeDepositEntity = depositJpaRepository.findByCode(depositHistory.getPayeeDepositCode())
			.orElseThrow(() -> ServiceErrorCode.DEPOSIT_NOT_FOUND
				.throwServiceException("depositCode: " + depositHistory.getPayeeDepositCode()));

		DepositHistoryEntity entity = DepositHistoryMapper.toEntity(
			depositHistory,
			depositEntity,
			payerDepositEntity,
			payeeDepositEntity
		);

		DepositHistoryEntity savedEntity = depositHistoryJpaRepository.save(entity);

		return DepositHistoryMapper.toDomain(savedEntity);
	}

	@Override
	public PageDto<DepositHistory> getDepositHistories(String userCode, PageQuery pageQuery) {
		log.debug("Getting deposit histories for userCode: {}", userCode);

		// PageQuery -> Spring PageRequest 변환
		PageRequest springPageable = PageRequest.of(pageQuery.page(), pageQuery.size());

		// JPA Repository 조회
		Page<DepositHistoryEntity> entityPage = depositHistoryJpaRepository.findByUserCode(userCode, springPageable);

		// Entity -> Domain 변환
		List<DepositHistory> items = entityPage.getContent().stream()
			.map(DepositHistoryMapper::toDomain)
			.collect(Collectors.toList());

		// Spring Page -> PageDto 변환
		return new PageDto<>(
			entityPage.getNumber(),
			entityPage.getSize(),
			entityPage.getTotalPages(),
			entityPage.getTotalElements(),
			items
		);
	}

	@Override
	public Optional<DepositHistory> getDepositHistoryByCode(String historyCode) {
		return depositHistoryJpaRepository.findByCode(historyCode)
			.map(DepositHistoryMapper::toDomain);
	}
}
