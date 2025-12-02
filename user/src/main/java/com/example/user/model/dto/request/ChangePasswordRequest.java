package com.example.user.model.dto.request;

public record ChangePasswordRequest(
	String password,
	String newPassword,
	String newPasswordCheck
) {
}
