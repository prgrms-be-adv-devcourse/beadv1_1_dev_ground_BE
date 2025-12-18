package io.devground.user.service;

import io.devground.user.model.dto.request.ChangePasswordRequest;
import io.devground.user.model.dto.request.EmailCertificationRequest;
import io.devground.user.model.dto.request.ModifyUserInfoRequest;
import io.devground.user.model.dto.request.UserRequest;
import io.devground.user.model.dto.response.ChangePasswordResponse;
import io.devground.user.model.dto.response.ModifyUserInfoResponse;
import io.devground.user.model.dto.response.UserResponse;
import io.devground.user.model.entity.User;
import jakarta.mail.MessagingException;

public interface UserService {
	void sendCertificateEmail(String email) throws MessagingException;

	void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest);

	Boolean isNicknameAvailable(String nickname);

	User registerUser(UserRequest userRequest);

	User getByUserCode(String userCode);

	UserResponse getByLoginUserCode(String userCode);

	void applyDepositCode(String userCode, String depositCode);

	void applyCartCode(String userCode, String cartCode);

	void deleteByUserCode(String userCode);

	void requestDeleteUser(String userCode);

	ChangePasswordResponse changePassword(String userCode, ChangePasswordRequest request);

	ModifyUserInfoResponse modifyUserInfo(String userCode, ModifyUserInfoRequest request);
}
