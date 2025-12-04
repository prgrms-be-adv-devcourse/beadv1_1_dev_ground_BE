package io.devground.dbay.ddddeposit.domain.port.in;

import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistory;
import io.devground.dbay.ddddeposit.domain.pagination.PageDto;
import io.devground.dbay.ddddeposit.domain.pagination.PageQuery;

public interface DepositHistoryUseCase {

	PageDto<DepositHistory> getDepositHistories(String depositCode, PageQuery pageQuery);

	DepositHistory getDepositHistoryByCode(String historyCode);
}
