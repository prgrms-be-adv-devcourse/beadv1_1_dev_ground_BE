package com.example.user.model.dto.request;

public record UserRequest(
	String name,
	String email,
	String password,
	String nickname,
	String phone,
	String address,
	String addressDetail
) {
}