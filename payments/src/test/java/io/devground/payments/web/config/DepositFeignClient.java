package io.devground.payments.web.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.core.model.web.BaseResponse;

/**
 * 테스트용 OpenFeign 클라이언트 인터페이스
 * @FeignClient 애노테이션 없이 순수 인터페이스로 정의
 * Feign.builder()로 동적으로 생성하여 사용
 */
@FeignClient(name = "depositClient", url = "http://localhost:8881")
public interface DepositFeignClient {

	@GetMapping("/api/deposits/balance")
	BaseResponse<DepositBalanceResponse> getBalance(@RequestHeader("X-CODE") String userCode);

	@GetMapping("/api/deposits")
	BaseResponse<DepositResponse> getDeposit(@RequestHeader("X-CODE") String userCode);
}
