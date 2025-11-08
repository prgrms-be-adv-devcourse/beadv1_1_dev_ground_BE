package com.example.user.model.entity;

import java.util.UUID;

import io.devground.core.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String password;

	private String email;

	@Setter
	private Long oauthId;

	private String nickname;

	private String phone;

	private String address;

	private String addressDetail;

	@Enumerated(EnumType.STRING)
	private RoleType role;


	@Builder
	public User(String name, String password, String email, String nickname, String address, String addressDetail, String phone){
		this.name = name;
		this.password = password;
		this.email = email;
		this.nickname = nickname;
		this.address = address;
		this.addressDetail = addressDetail;
		this.phone = phone;
		this.role = RoleType.USER;
	}

}
