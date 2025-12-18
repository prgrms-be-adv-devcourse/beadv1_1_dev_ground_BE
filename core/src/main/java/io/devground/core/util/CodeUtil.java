package io.devground.core.util;

import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CodeUtil {

	public String generateUUID() {
		return UUID.randomUUID().toString();
	}
}
