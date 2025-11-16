package io.devground.core.model.web;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
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

		ErrorCode errorCode = ErrorCode.fromHttpStatus(resultCode, msg);

		throw new ServiceException(errorCode);
	}
}