package io.devground.dbay.domain.settlement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.settlement.model.entity.Settlement;
import io.devground.dbay.domain.settlement.model.entity.vo.SettlementStatus;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

	/**
	 * 판매자별 정산 조회
	 */
	Page<Settlement> findBySellerCode(String sellerCode, Pageable pageable);

	/**
	 * 정산 상태별 조회
	 */
	Page<Settlement> findBySettlementStatus(SettlementStatus settlementStatus, Pageable pageable);
}
