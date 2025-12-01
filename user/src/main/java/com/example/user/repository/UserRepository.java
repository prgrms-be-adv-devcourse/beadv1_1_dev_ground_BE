package com.example.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.user.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByCode(String code);

	User findByEmail(String email);

	Boolean existsByEmail(String email);

	Boolean existsByEmailAndOauthIdIsNull(String email);

	Optional<User> findByOauthId(Long oauthId);
}
