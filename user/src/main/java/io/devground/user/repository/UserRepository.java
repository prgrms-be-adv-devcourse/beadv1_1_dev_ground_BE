package io.devground.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.user.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByCode(String code);

	Optional<User> findByCodeAndDeleteStatus(String code, DeleteStatus deleteStatus);

	User findByEmail(String email);

	Boolean existsByEmail(String email);

	Boolean existsByEmailAndOauthIdIsNull(String email);

	Optional<User> findByOauthId(Long oauthId);
}
