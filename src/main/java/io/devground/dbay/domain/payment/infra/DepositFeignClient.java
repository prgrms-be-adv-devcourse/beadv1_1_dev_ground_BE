package io.devground.dbay.domain.payment.infra;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.model.web.BaseResponse;

@FeignClient(
	name = "PaymentToDeposit",
	url = "${external.openfeign-url}",
	path = "/api/deposits"
)
public interface DepositFeignClient {

	@GetMapping("/balance")
	BaseResponse<DepositBalanceResponse> getBalance(String userCode);
}
