package io.devground.payments.deposit.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.dto.deposit.response.DepositHistoryResponse;
import io.devground.core.model.web.BaseResponse;

import io.devground.payments.deposit.domain.depositHistory.DepositHistory;
import io.devground.payments.deposit.domain.pagination.PageDto;
import io.devground.payments.deposit.domain.pagination.PageQuery;
import io.devground.payments.deposit.domain.port.in.DepositHistoryUseCase;
import io.devground.payments.deposit.infrastructure.mapper.DepositHistoryMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deposit-histories")
public class DepositHistoryController {

	private final DepositHistoryUseCase depositHistoryUseCase;

	@GetMapping
	public BaseResponse<PageDto<DepositHistoryResponse>> getDepositHistories(
		@RequestHeader("X-CODE") String userCode, PageQuery pageQuery) {

		PageDto<DepositHistory> histories = depositHistoryUseCase.getDepositHistories(userCode, pageQuery);

		// Domain PageDto -> Response PageDto 변환
		PageDto<DepositHistoryResponse> response = new PageDto<>(
			histories.currentPageNumber(),
			histories.pageSize(),
			histories.totalPages(),
			histories.totalItems(),
			histories.items().stream()
				.map(DepositHistoryMapper::toDepositHistoryResponse)
				.toList()
		);

		return BaseResponse.success(200, response, "거래 내역 조회 성공");
	}

	@GetMapping("/{historyCode}")
	public BaseResponse<DepositHistoryResponse> getDepositHistory(@PathVariable String historyCode) {
		DepositHistory history = depositHistoryUseCase.getDepositHistoryByCode(historyCode);
		DepositHistoryResponse response = DepositHistoryMapper.toDepositHistoryResponse(history);

		return BaseResponse.success(200, response, "거래 내역 조회 성공");
	}
}
