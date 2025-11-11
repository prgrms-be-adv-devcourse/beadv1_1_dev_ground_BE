package io.devground.core.model.web;

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

	public static <T> BaseResponse<T> success(int resultCode, String msg) {
		return new BaseResponse<>(resultCode, msg, null);
	}

	public static <T> BaseResponse<T> fail(int resultCode, String msg) {
		return new BaseResponse<>(resultCode, msg, null);
	}
}