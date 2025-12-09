package io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.devground.dbay.common.exceptionhandler.GlobalExceptionHandler;
import io.devground.dbay.ddddeposit.application.exception.ServiceException;
import io.devground.dbay.ddddeposit.application.exception.vo.ServiceErrorCode;
import io.devground.dbay.ddddeposit.application.service.DepositApplication;
import io.devground.dbay.ddddeposit.domain.deposit.Deposit;
import io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.DepositController;

@WebMvcTest(DepositController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("DepositController API 테스트")
class DepositControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DepositApplication depositApplication;

	@Nested
	@DisplayName("GET /api/deposits/balance")
	class DescribeGetBalance {

		@Test
		@DisplayName("성공 케이스 - 유효한 사용자 코드로 잔액 조회 시 200 OK와 잔액을 반환한다")
		void givenValidUserCode_whenGetBalance_thenReturns200WithBalance() throws Exception {
			// given
			String userCode = "USER001";
			Long expectedBalance = 10000L;
			Deposit deposit = new Deposit(userCode);
			deposit.charge(expectedBalance);

			given(depositApplication.getByUserCode(userCode)).willReturn(deposit);

			// when & then
			mockMvc.perform(get("/api/deposits/balance")
					.header("X-CODE", userCode)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.resultCode").value(200))
				.andExpect(jsonPath("$.msg").value("잔액 조회 성공"))
				.andExpect(jsonPath("$.data.balance").value(expectedBalance));

			then(depositApplication).should(times(1)).getByUserCode(userCode);
		}

		@Test
		@DisplayName("실패 케이스 - 존재하지 않는 사용자 코드로 조회 시 HTTP 404와 resultCode 404를 반환한다")
		void givenInvalidUserCode_whenGetBalance_thenReturnsHttp404WithResultCode404() throws Exception {
			// given
			String invalidUserCode = "INVALID_USER";

			given(depositApplication.getByUserCode(invalidUserCode))
				.willThrow(new ServiceException(ServiceErrorCode.DEPOSIT_NOT_FOUND));

			// when & then
			mockMvc.perform(get("/api/deposits/balance")
					.header("X-CODE", invalidUserCode)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())  // HTTP는 404
				.andExpect(jsonPath("$.resultCode").value(404))  // BaseResponse의 resultCode는 404
				.andExpect(jsonPath("$.msg").value("예치금 계정을 찾을 수 없습니다. "))
				.andExpect(jsonPath("$.data").isEmpty());

			then(depositApplication).should(times(1)).getByUserCode(invalidUserCode);
		}

		@Test
		@DisplayName("실패 케이스 - X-CODE 헤더가 없으면 403 Forbidden을 반환한다")
		void givenNoUserCodeHeader_whenGetBalance_thenReturns403() throws Exception {
			// when & then
			mockMvc.perform(get("/api/deposits/balance")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

			then(depositApplication).should(never()).getByUserCode(any());
		}
	}

	@Nested
	@DisplayName("GET /api/deposits")
	class DescribeGetDeposit {

		@Test
		@DisplayName("성공 케이스 - 유효한 사용자 코드로 예치금 계정 조회 시 200 OK와 계정 정보를 반환한다")
		void givenValidUserCode_whenGetDeposit_thenReturns200WithDepositInfo() throws Exception {
			// given
			String userCode = "USER001";
			Long balance = 10000L;
			Deposit deposit = new Deposit(userCode);
			deposit.charge(balance);

			given(depositApplication.getByUserCode(userCode)).willReturn(deposit);

			// when & then
			mockMvc.perform(get("/api/deposits")
					.header("X-CODE", userCode)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.resultCode").value(200))
				.andExpect(jsonPath("$.msg").value("예치금 계정 조회 성공"))
				.andExpect(jsonPath("$.data.userCode").value(userCode))
				.andExpect(jsonPath("$.data.balance").value(balance))
				.andExpect(jsonPath("$.data.depositCode").exists());

			then(depositApplication).should(times(1)).getByUserCode(userCode);
		}

		@Test
		@DisplayName("실패 케이스 - 존재하지 않는 사용자 코드로 조회 시 HTTP 404와 resultCode 404를 반환한다")
		void givenInvalidUserCode_whenGetDeposit_thenReturnsHttp404WithResultCode404() throws Exception {
			// given
			String invalidUserCode = "INVALID_USER";

			given(depositApplication.getByUserCode(invalidUserCode))
				.willThrow(new ServiceException(ServiceErrorCode.DEPOSIT_NOT_FOUND));

			// when & then
			mockMvc.perform(get("/api/deposits")
					.header("X-CODE", invalidUserCode)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())  // HTTP는 404
				.andExpect(jsonPath("$.resultCode").value(404))  // BaseResponse의 resultCode는 404
				.andExpect(jsonPath("$.msg").value("예치금 계정을 찾을 수 없습니다. "))
				.andExpect(jsonPath("$.data").isEmpty());

			then(depositApplication).should(times(1)).getByUserCode(invalidUserCode);
		}

		@Test
		@DisplayName("실패 케이스 - X-CODE 헤더가 없으면 403 Forbidden을 반환한다")
		void givenNoUserCodeHeader_whenGetDeposit_thenReturns403() throws Exception {
			// when & then
			mockMvc.perform(get("/api/deposits")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

			then(depositApplication).should(never()).getByUserCode(any());
		}
	}
}