package io.devground.core.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
	USER("USER"),
	ADMIN("ADMIN");

	private final String text;

	public static RoleType fromText(String text) {
		for (RoleType role : values()) {
			if (role.getText().equals(text)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unexpected text: " + text);
	}
}
