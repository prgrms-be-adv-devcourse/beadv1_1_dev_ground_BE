package io.devground.dbay.domain.payment.infra;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;

@FeignClient(
	name = "PaymentToDeposit",
	url = "http://localhost:8000",
	path = "/api/deposits"
)
public interface DepositFeignClient {
	@GetMapping("/balance")
	BaseResponse<DepositBalanceResponse> getBalance(@RequestHeader("X-CODE") String userCode);
}
