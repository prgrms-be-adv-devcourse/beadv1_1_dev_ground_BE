package com.example.user.model.dto.request;

public record LoginRequest(
	String email,
	String password
) {
}
