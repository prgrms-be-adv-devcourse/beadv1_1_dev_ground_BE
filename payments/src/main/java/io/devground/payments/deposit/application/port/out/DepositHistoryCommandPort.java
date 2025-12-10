package io.devground.payments.deposit.application.port.out;

import java.util.Optional;

import io.devground.payments.deposit.domain.depositHistory.DepositHistory;
import io.devground.payments.deposit.domain.pagination.PageDto;
import io.devground.payments.deposit.domain.pagination.PageQuery;

public interface DepositHistoryCommandPort {

	DepositHistory saveDepositHistory(DepositHistory depositHistory);

	PageDto<DepositHistory> getDepositHistories(String depositCode, PageQuery pageQuery);

	Optional<DepositHistory> getDepositHistoryByCode(String historyCode);
}
