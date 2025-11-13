package com.example.user.service;

import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.entity.User;

public interface UserService {
	void sendCertificateEmail(String email);

	void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest);

	User registerUser(UserRequest userRequest);
}
