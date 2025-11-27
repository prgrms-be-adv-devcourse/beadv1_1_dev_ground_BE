package com.example.user.service;

import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.entity.User;

import jakarta.mail.MessagingException;

public interface UserService {
	void sendCertificateEmail(String email) throws MessagingException;

	void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest);

	User registerUser(UserRequest userRequest);

	User getByUserCode(String userCode);

	void applyDepositCode(String userCode, String depositCode);

	void applyCartCode(String userCode, String cartCode);

	void deleteByUserCode(String userCode);

	void requestDeleteUser(String userCode);
}
