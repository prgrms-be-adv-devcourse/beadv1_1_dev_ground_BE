package io.devground.core.util;

import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Validators {
	// UUID 포맷 검증을 위한 공통 메서드
	public boolean isValidUuid(String code) {
		if (code == null)
			return false;
		try {
			UUID.fromString(code);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
