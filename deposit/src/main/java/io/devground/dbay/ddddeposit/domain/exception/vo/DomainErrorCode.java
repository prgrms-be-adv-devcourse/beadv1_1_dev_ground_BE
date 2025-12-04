package io.devground.dbay.ddddeposit.domain.exception.vo;

import io.devground.dbay.ddddeposit.domain.exception.DomainException;

public enum DomainErrorCode {
	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// common
	PAGE_MUST_BE_POSITIVE(400, "페이지 번호는 0보다 커야합니다."),
	PAGE_SIZE_MUST_BE_POSITIVE(400, "페이지 사이즈는 0보다 커야합니다."),

	// deposit
	DEPOSIT_ALREADY_EXISTS(409, "이미 예금 계정이 존재합니다."),
	DEPOSIT_NOT_FOUND(404, "예치금 계정을 찾을 수 없습니다."),
	DEPOSIT_HISTORY_NOT_FOUND(404, "거래 내역을 찾을 수 없습니다."),
	AMOUNT_MUST_BE_POSITIVE(400, "금액은 0보다 커야 합니다."),
	INSUFFICIENT_BALANCE(400, "잔액이 부족합니다.");


	private final int httpStatus;
	private final String message;

	DomainErrorCode(int httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public DomainException throwException() {
		throw new DomainException(this);
	}

	public DomainException throwException(Throwable cause) {
		throw new DomainException(this, cause);
	}

	public static DomainErrorCode fromHttpStatus(int httpStatus, String msg) {
		for (DomainErrorCode errorCode : DomainErrorCode.values()) {
			if (errorCode.getHttpStatus() == httpStatus && errorCode.getMessage().equals(msg)) {
				return errorCode;
			}
		}

		return INTERNAL_SERVER_ERROR;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getMessage() {
		return message;
	}
}
