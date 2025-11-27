package io.devground.payment.infra;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.devground.core.model.web.BaseResponse;
import io.devground.payment.model.dto.response.DepositBalanceResponse;

@FeignClient(
	name = "PaymentToDeposit",
	url = "${external.openfeign-url}",
	path = "/api/deposits"
)
public interface DepositFeignClient {
	@GetMapping("/balance")
	//TODO: DepositBalanceResponse를 core로 옮겨야 함
	BaseResponse<DepositBalanceResponse> getBalance(String userCode);
}
