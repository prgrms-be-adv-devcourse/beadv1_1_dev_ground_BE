package io.devground.dbay.domain.settlement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.dbay.domain.settlement.mapper.SettlementMapper;
import io.devground.dbay.domain.settlement.model.dto.request.CreateSettlementRequest;
import io.devground.dbay.domain.settlement.model.dto.response.SettlementResponse;
import io.devground.dbay.domain.settlement.model.entity.Settlement;
import io.devground.dbay.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementServiceImpl implements SettlementService {

	private final SettlementRepository settlementRepository;

	@Value("${custom.settlement.rate}")
	private Double settlementRate;

	@Override
	public Page<SettlementResponse> getSettlementsBySeller(String sellerCode, Pageable pageable) {
		return settlementRepository.findBySellerCode(sellerCode, pageable)
			.map(SettlementMapper::toSettlementResponse);
	}

	@Override
	@Transactional
	public SettlementResponse createSettlement(CreateSettlementRequest request) {
		Settlement settlement = Settlement.builder()
			.orderCode(request.orderCode())
			.orderItemCode(request.orderItemCode())
			.buyerCode(request.userCode())
			.sellerCode(request.sellerCode())
			.settlementDate(LocalDateTime.now())
			.settlementRate(settlementRate)
			.totalAmount(request.productPrice())
			.build();

		// 정산 정책 적용 (수수료 및 잔액 계산)
		settlement.applySettlementPolicy();

		Settlement savedSettlement = settlementRepository.save(settlement);

		return SettlementMapper.toSettlementResponse(savedSettlement);
	}
}
