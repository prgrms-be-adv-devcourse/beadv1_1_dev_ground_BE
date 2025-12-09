package io.devground.user.model.entity;

import io.devground.core.model.entity.BaseEntity;

import io.devground.core.model.entity.RoleType;
import jakarta.persistence.Column;
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

	@Column(nullable = false)
	private String name;

	@Setter
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String email;

	@Setter
	private Long oauthId;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String addressDetail;

	@Setter
	//    @Column(unique = true, nullable = false)
	private String cartCode;

	@Setter
	//    @Column(unique = true, nullable = false)
	private String depositCode;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RoleType role;

	private String profileImage;

	private String username;

	@Builder
	public User(String name, String password, Long oauthId, String email, String nickname, String address, String addressDetail,
				String phone, RoleType role, String profileImage) {
		this.name = name;
		this.password = password;
		this.oauthId = oauthId;
		this.email = email;
		this.nickname = nickname;
		this.address = address;
		this.addressDetail = addressDetail;
		this.phone = phone;
		this.role = role;
		this.profileImage = profileImage;
	}

	public void modifyUser(String nickname, String phone, String address, String addressDetail){
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (phone != null) {
			this.phone = phone;
		}
		if (address != null) {
			this.address = address;
		}
		if (addressDetail != null) {
			this.addressDetail = addressDetail;
		}
	}

}