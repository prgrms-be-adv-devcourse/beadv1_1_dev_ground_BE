package io.devground.dbay.ddddeposit.application.exception.vo;

import io.devground.dbay.ddddeposit.application.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceErrorCode {
	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
	// deposit
	DEPOSIT_ALREADY_EXISTS(409, "이미 예금 계정이 존재합니다."),
	DEPOSIT_NOT_FOUND(404, "예치금 계정을 찾을 수 없습니다."),
	DEPOSIT_HISTORY_NOT_FOUND(404, "거래 내역을 찾을 수 없습니다."),
	AMOUNT_MUST_BE_POSITIVE(400, "금액은 0보다 커야 합니다."),
	INSUFFICIENT_BALANCE(400, "잔액이 부족합니다.");

	private final int httpStatus;
	private final String message;

	public ServiceException throwServiceException() {
		throw new ServiceException(this);
	}

	public ServiceException throwServiceException(Throwable cause) {
		throw new ServiceException(this, cause);
	}

	public static ServiceErrorCode fromHttpStatus(int httpStatus, String msg) {
		for (ServiceErrorCode errorCode : ServiceErrorCode.values()) {
			if (errorCode.getHttpStatus() == httpStatus && errorCode.getMessage().equals(msg)) {
				return errorCode;
			}
		}

		return INTERNAL_SERVER_ERROR;
	}
}
