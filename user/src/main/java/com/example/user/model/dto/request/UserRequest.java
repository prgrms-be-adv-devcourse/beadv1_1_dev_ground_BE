package com.example.user.model.dto.request;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.user.model.entity.RoleType;
import com.example.user.model.entity.User;

public record UserRequest(
	String name,
	String email,
	String password,
	String nickname,
	String phone,
	String address,
	String addressDetail
) {
	public static User from(UserRequest userRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {
		return User.builder()
			.email(userRequest.email())
			.name(userRequest.name())
			.password(bCryptPasswordEncoder.encode(userRequest.password()))
			.nickname(userRequest.nickname())
			.phone(userRequest.phone())
			.address(userRequest.address())
			.addressDetail(userRequest.addressDetail())
			.role(RoleType.USER)
			.profileImage("")
			.build();
	}
}