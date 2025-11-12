package com.example.user.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.user.model.dto.request.UserRequest;
import io.devground.core.model.entity.RoleType;
import com.example.user.model.entity.User;

public class UserMapper {
	public static User toEntity(UserRequest userRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {
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
