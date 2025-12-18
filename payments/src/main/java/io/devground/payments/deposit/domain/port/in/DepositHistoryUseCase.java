package io.devground.payments.deposit.domain.port.in;

import io.devground.payments.deposit.domain.depositHistory.DepositHistory;
import io.devground.payments.deposit.domain.pagination.PageDto;
import io.devground.payments.deposit.domain.pagination.PageQuery;

public interface DepositHistoryUseCase {

	PageDto<DepositHistory> getDepositHistories(String depositCode, PageQuery pageQuery);

	DepositHistory getDepositHistoryByCode(String historyCode);
}
