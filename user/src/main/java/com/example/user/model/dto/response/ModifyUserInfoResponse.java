package com.example.user.model.dto.response;

public record ModifyUserInfoResponse(
	String nickname,
	String phone,
	String address,
	String addressDetail
) {
}
