package io.devground.product.infrastructure.vo;

import io.devground.product.infrastructure.exception.InfraException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfraErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// common
	CODE_INVALID(400, "잘못된 코드 형식입니다."),
	METHOD_NOT_ALLOWED(400, "잘못된 Http Method 입니다."),
	PAGE_MUST_BE_POSITIVE(400, "페이지 번호는 0보다 커야합니다."),
	PAGE_SIZE_MUST_BE_POSITIVE(400, "페이지 사이즈는 0보다 커야합니다."),

	// saga
	SAGA_NOT_FOUND(404, "사가 정보를 찾을 수 없습니다.");

	private final int httpStatus;
	private final String message;

	public InfraException throwException() {
		throw new InfraException(this);
	}

	public InfraException throwException(Throwable cause) {
		throw new InfraException(this, cause);
	}

	public static InfraErrorCode fromHttpStatus(int httpStatus, String msg) {
		for (InfraErrorCode errorCode : InfraErrorCode.values()) {
			if (errorCode.getHttpStatus() == httpStatus && errorCode.getMessage().equals(msg)) {
				return errorCode;
			}
		}

		return INTERNAL_SERVER_ERROR;
	}
}
