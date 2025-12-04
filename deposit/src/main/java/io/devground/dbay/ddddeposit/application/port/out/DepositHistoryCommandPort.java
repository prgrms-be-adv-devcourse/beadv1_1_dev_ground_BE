package io.devground.dbay.ddddeposit.application.port.out;

import java.util.Optional;

import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistory;
import io.devground.dbay.ddddeposit.domain.pagination.PageDto;
import io.devground.dbay.ddddeposit.domain.pagination.PageQuery;

public interface DepositHistoryCommandPort {

	DepositHistory saveDepositHistory(DepositHistory depositHistory);

	PageDto<DepositHistory> getDepositHistories(String depositCode, PageQuery pageQuery);

	Optional<DepositHistory> getDepositHistoryByCode(String historyCode);
}
