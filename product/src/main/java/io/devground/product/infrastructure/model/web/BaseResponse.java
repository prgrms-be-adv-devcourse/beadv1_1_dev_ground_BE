package io.devground.product.infrastructure.model.web;

import io.devground.product.domain.exception.DomainException;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.infrastructure.exception.InfraException;
import io.devground.product.infrastructure.vo.InfraErrorCode;
import lombok.NonNull;

public record BaseResponse<T>(

	int resultCode,

	@NonNull
	String msg,

	T data
) {
	public static <T> BaseResponse<T> success(int resultCode, T data, String msg) {
		return new BaseResponse<>(resultCode, msg, data);
	}

	public static <T> BaseResponse<T> success(int resultCode, T data) {
		return success(resultCode, data, "");
	}

	public static <T> BaseResponse<T> success(int resultCode, String msg) {
		return success(resultCode, null, msg);
	}

	public static <T> BaseResponse<T> fail(int resultCode, String msg) {
		return new BaseResponse<>(resultCode, msg, null);
	}

	public boolean isSuccess() {

		return resultCode >= 200 && resultCode < 400;
	}

	public BaseResponse<T> throwIfNotSuccess() {

		if (isSuccess()) {
			return this;
		}

		DomainErrorCode domainErrorCode = DomainErrorCode.fromHttpStatus(resultCode, msg);
		if (domainErrorCode != DomainErrorCode.INTERNAL_SERVER_ERROR) {
			throw new DomainException(domainErrorCode);
		}

		InfraErrorCode infraErrorCode = InfraErrorCode.fromHttpStatus(resultCode, msg);
		throw new InfraException(infraErrorCode);
	}
}