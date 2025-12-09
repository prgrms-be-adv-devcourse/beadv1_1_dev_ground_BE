package io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.config.DepositFeignClient;
import io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.config.TestFeignClientConfig;
import io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.config.TestWireMockConfig;

@SpringBootTest(
	classes = {
		FeignAutoConfiguration.class,
		HttpMessageConvertersAutoConfiguration.class,
		TestFeignClientConfig.class
	},
	properties = {
		"feign.client.config.feign-deposit.url=http://localhost:8881"
	}
)
@Import(TestWireMockConfig.class)
class DepositControllerOpenFeignTest {

	@Autowired
	private WireMockServer mockUserMicroService;

	@Autowired
	private DepositFeignClient depositFeignClient;

	@BeforeEach
	void setUp() {
		mockUserMicroService.resetAll();
	}

	@AfterEach
	void tearDown() {
		mockUserMicroService.resetAll();
	}

	@Test
	@DisplayName("예치금 계정 조회 - WireMock stub 테스트")
	void getDeposit_WithStub_Success() {
		// given
		String userCode = "USER123";
		String stubResponse = """
			{
				"resultCode": 200,
				"msg": "예치금 계정 조회 성공",
				"data": {
					"userCode": "USER123",
					"depositCode": "DEP001",
					"balance": 50000,
					"createdAt": "2025-01-01T00:00:00",
					"updatedAt": "2025-01-01T00:00:00"
				}
			}
			""";

		mockUserMicroService.stubFor(get(urlEqualTo("/api/deposits"))
			.withHeader("X-CODE", equalTo(userCode))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody(stubResponse)));

		// when
		BaseResponse<DepositResponse> response = depositFeignClient.getDeposit(userCode);

		// then
		assertThat(response).isNotNull();
		assertThat(response.resultCode()).isEqualTo(200);
		assertThat(response.data()).isNotNull();
		assertThat(response.data().userCode()).isEqualTo(userCode);
		assertThat(response.data().balance()).isEqualTo(50000L);
		assertThat(response.msg()).isEqualTo("예치금 계정 조회 성공");

		// verify
		mockUserMicroService.verify(getRequestedFor(urlEqualTo("/api/deposits"))
			.withHeader("X-CODE", equalTo(userCode)));
	}

	@Test
	@DisplayName("잔액 조회 - WireMock stub 테스트")
	void getBalance_WithStub_Success() {
		// given
		String userCode = "USER456";
		String stubResponse = """
			{
				"resultCode": 200,
				"msg": "잔액 조회 성공",
				"data": {
					"userCode": "USER456",
					"balance": 100000
				}
			}
			""";

		mockUserMicroService.stubFor(get(urlEqualTo("/api/deposits/balance"))
			.withHeader("X-CODE", equalTo(userCode))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody(stubResponse)));

		// when
		BaseResponse<DepositBalanceResponse> response = depositFeignClient.getBalance(userCode);

		// then
		assertThat(response).isNotNull();
		assertThat(response.resultCode()).isEqualTo(200);
		assertThat(response.data()).isNotNull();
		assertThat(response.data().balance()).isEqualTo(100000L);
		assertThat(response.msg()).isEqualTo("잔액 조회 성공");

		// verify
		mockUserMicroService.verify(getRequestedFor(urlEqualTo("/api/deposits/balance"))
			.withHeader("X-CODE", equalTo(userCode)));
	}

	@Test
	@DisplayName("예치금 계정 조회 실패 - 404 에러 stub 테스트")
	void getDeposit_WithStub_NotFound() {
		// given
		String userCode = "INVALID_USER";
		String stubResponse = """
			{
				"resultCode": 404,
				"msg": "예치금 계정을 찾을 수 없습니다",
				"data": null
			}
			""";

		mockUserMicroService.stubFor(get(urlEqualTo("/api/deposits"))
			.withHeader("X-CODE", equalTo(userCode))
			.willReturn(aResponse()
				.withStatus(404)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody(stubResponse)));

		// when & then
		assertThatThrownBy(() -> depositFeignClient.getDeposit(userCode))
			.isInstanceOf(ServiceException.class);

		// verify
		mockUserMicroService.verify(getRequestedFor(urlEqualTo("/api/deposits"))
			.withHeader("X-CODE", equalTo(userCode)));
	}

	@Test
	@DisplayName("잔액 조회 실패 - 500 에러 stub 테스트")
	void getBalance_WithStub_ServerError() {
		// given
		String userCode = "USER789";
		String stubResponse = """
			{
				"resultCode": 500,
				"msg": "Internal Server Error",
				"data": null
			}
			""";

		mockUserMicroService.stubFor(get(urlEqualTo("/api/deposits/balance"))
			.withHeader("X-CODE", equalTo(userCode))
			.willReturn(aResponse()
				.withStatus(500)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody(stubResponse)));

		// when & then
		assertThatThrownBy(() -> depositFeignClient.getBalance(userCode))
			.isInstanceOf(ServiceException.class);

		// verify
		mockUserMicroService.verify(getRequestedFor(urlEqualTo("/api/deposits/balance"))
			.withHeader("X-CODE", equalTo(userCode)));
	}
}