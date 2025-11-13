package com.example.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.auth.model.dto.CustomUserDetails;
import com.example.user.model.entity.User;
import com.example.user.repository.UserRepository;

import io.devground.core.model.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);

		log.info("user:{}", user);
		if (user != null) {
			return new CustomUserDetails(user);
		}

		throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
	}
}
