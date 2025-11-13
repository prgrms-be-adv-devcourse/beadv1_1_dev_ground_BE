package io.devground.dbay.domain.deposit.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.service.DepositServiceImpl;

@DisplayName("API 컨트롤러 - 예치금")
@WebMvcTest(DepositController.class)
class DepositControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DepositServiceImpl depositService;

	@DisplayName("[view][GET] 예치금 조회 - 정상 호출")
	@Test
	void givenUserCode_whenGettingDeposit_thenReturnsDepositResponse() throws Exception {
		// given
		String userCode = "USER_CODE";
		Long balance = 1000L;
		String depositCode =  "DEPOSIT_CODE";
		DepositResponse response = new DepositResponse(
			1L,
			userCode,
			depositCode,
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
			.andExpect(jsonPath("$.data.balance").value(1000L));

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

}
