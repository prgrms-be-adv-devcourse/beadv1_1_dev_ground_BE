package io.devground.dbay.domain.deposit.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.dbay.domain.deposit.dto.request.ChargeRequest;
import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.domain.deposit.service.DepositServiceImpl;

@DisplayName("API 컨트롤러 - 예치금")
@WebMvcTest(DepositController.class)
class DepositControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private DepositServiceImpl depositService;

	@DisplayName("[view][POST] 예치금 생성 - 정상 호출")
	@Test
	void givenUserCode_whenCreatingDeposit_thenReturnsDepositResponse() throws Exception {
		// given
		String userCode = "USER_CODE";
		DepositResponse response = new DepositResponse(
			1L,
			userCode,
			0L,
			LocalDateTime.now(),
			LocalDateTime.now()
		);

		given(depositService.createDeposit(userCode))
			.willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(post("/api/deposits")
			.header("X-CODE", userCode));

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value(201))
			.andExpect(jsonPath("$.data.userCode").value(userCode))
			.andExpect(jsonPath("$.data.balance").value(0L))
			.andExpect(jsonPath("$.msg").value("예금 계정이 생성되었습니다."));

		then(depositService).should().createDeposit(userCode);
	}

	@DisplayName("[view][GET] 예치금 조회 - 정상 호출")
	@Test
	void givenUserCode_whenGettingDeposit_thenReturnsDepositResponse() throws Exception {
		// given
		String userCode = "USER_CODE";
		Long balance = 1000L;
		DepositResponse response = new DepositResponse(
			1L,
			userCode,
			balance,
			LocalDateTime.now(),
			LocalDateTime.now()
		);

		given(depositService.getByUserCode(userCode))
			.willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(get("/api/deposits")
			.header("X-CODE", userCode));

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value(200))
			.andExpect(jsonPath("$.data.userCode").value(userCode))
			.andExpect(jsonPath("$.data.balance").value(1000L))
			.andExpect(jsonPath("$.msg").value("예금 계정 조회 성공"));

		then(depositService).should().getByUserCode(userCode);
	}

	@DisplayName("[view][GET] 잔액 조회 - 정상 호출")
	@Test
	void givenUserCode_whenGettingBalance_thenReturnsBalanceResponse() throws Exception {
		// given
		String userCode = "USER_CODE";
		Long expectedBalance = 10000L;
		DepositBalanceResponse response = new DepositBalanceResponse(expectedBalance);

		given(depositService.getByBalance(userCode))
			.willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(get("/api/deposits/balance")
			.header("X-CODE", userCode));

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value(200))
			.andExpect(jsonPath("$.data.balance").value(10000L))
			.andExpect(jsonPath("$.msg").value("잔액 조회 성공"));

		then(depositService).should().getByBalance(userCode);
	}

	@DisplayName("[view][POST] 예치금 충전 - 정상 호출")
	@Test
	void givenValidChargeRequest_whenChargingDeposit_thenReturnsDepositHistoryResponse() throws Exception {
		// given
		String userCode = "USER_CODE";
		Long chargeAmount = 10000L;
		ChargeRequest request = new ChargeRequest(chargeAmount, "토스 충전");

		DepositHistoryResponse response = new DepositHistoryResponse(
			1L,
			1L,
			userCode,
			null,
			1L,
			chargeAmount,
			10000L,
			DepositHistoryType.CHARGE_TOSS,
			"토스 충전",
			LocalDateTime.now()
		);

		given(depositService.charge(userCode, DepositHistoryType.CHARGE_TOSS, chargeAmount))
			.willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(post("/api/deposits/charge")
			.header("X-CODE", userCode)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value(200))
			.andExpect(jsonPath("$.data.amount").value(chargeAmount))
			.andExpect(jsonPath("$.data.balanceAfter").value(10000L))
			.andExpect(jsonPath("$.data.type").value("CHARGE_TOSS"))
			.andExpect(jsonPath("$.msg").value("충전이 완료되었습니다."));

		then(depositService).should().charge(userCode, DepositHistoryType.CHARGE_TOSS, chargeAmount);
	}

	@DisplayName("[view][POST] 예치금 충전 금액이 null인 경우 - 실패호출")
	@Test
	void givenNullAmount_whenChargingDeposit_thenReturnsBadRequest() throws Exception {
		// given
		String userCode = "USER_CODE";
		ChargeRequest request = new ChargeRequest(null, "토스 충전");

		// when
		ResultActions actions = mockMvc.perform(post("/api/deposits/charge")
			.header("X-CODE", userCode)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		actions.andExpect(status().isBadRequest());
		then(depositService).should(never()).charge(anyString(), any(DepositHistoryType.class), anyLong());
	}

	@DisplayName("[view][POST] 예치금 충전 금액이 0 이하인 경우 - 실패호출")
	@Test
	void givenNegativeOrZeroAmount_whenChargingDeposit_thenReturnsBadRequest() throws Exception {
		// given
		String userCode = "USER_CODE";
		ChargeRequest request = new ChargeRequest(-1000L, "토스 충전");

		// when
		ResultActions actions = mockMvc.perform(post("/api/deposits/charge")
			.header("X-CODE", userCode)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		actions.andExpect(status().isBadRequest());
		then(depositService).should(never()).charge(anyString(), any(DepositHistoryType.class), anyLong());
	}

}