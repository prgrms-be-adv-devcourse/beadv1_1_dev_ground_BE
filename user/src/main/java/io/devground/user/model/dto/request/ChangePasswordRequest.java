package io.devground.user.model.dto.request;

public record ChangePasswordRequest(
	String password,
	String newPassword,
	String newPasswordCheck
) {
}
