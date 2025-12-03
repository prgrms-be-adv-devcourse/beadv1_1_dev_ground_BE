package io.devground.product.domain.util;

import java.util.UUID;

public abstract class DomainUtils {

	public static String generateCode() {
		return UUID.randomUUID().toString();
	}
}
