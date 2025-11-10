package com.example.user.service;

import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.UserRequest;

public interface UserService {
	void sendCertificateEmail(String email);

	void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest);

	void registerUser(UserRequest userRequest);
}
