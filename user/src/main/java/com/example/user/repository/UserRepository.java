package com.example.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user.model.entity.User;

import io.devground.core.model.vo.DeleteStatus;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByCode(String code);

	Optional<User> findByCodeAndDeleteStatus(String code, DeleteStatus deleteStatus);

	User findByEmail(String email);

	Boolean existsByEmail(String email);

	Boolean existsByEmailAndOauthIdIsNull(String email);

	Optional<User> findByOauthId(Long oauthId);
}
