package com.example.user.model.dto.request;

public record EmailCertificationRequest(
	String email,
	String code
) {
}