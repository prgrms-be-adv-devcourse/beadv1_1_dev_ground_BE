package io.devground.dbay.domain.settlement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.dbay.domain.settlement.mapper.SettlementMapper;
import io.devground.dbay.domain.settlement.model.dto.response.SettlementResponse;
import io.devground.dbay.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementServiceImpl implements SettlementService {

	private final SettlementRepository settlementRepository;

	@Override
	public Page<SettlementResponse> getSettlementsBySeller(String sellerCode, Pageable pageable) {
		return settlementRepository.findBySellerCode(sellerCode, pageable)
			.map(SettlementMapper::toSettlementResponse);
	}
}
