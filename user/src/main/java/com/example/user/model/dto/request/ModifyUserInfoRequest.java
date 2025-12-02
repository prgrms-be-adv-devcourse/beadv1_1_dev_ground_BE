package com.example.user.model.dto.request;


public record ModifyUserInfoRequest(
	String nickname,
	String phone,
	String address,
	String addressDetail
) {
}
