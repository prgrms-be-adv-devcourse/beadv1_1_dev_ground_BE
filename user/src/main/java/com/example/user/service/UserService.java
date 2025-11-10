package com.example.user.service;

import com.example.user.model.dto.request.EmailCertificationRequest;

public interface UserService {
	void sendCertificateEmail(String email);

	void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest);
}
