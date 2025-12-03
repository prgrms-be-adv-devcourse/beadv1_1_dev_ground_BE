package io.devground.product.domain.util;

import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DomainUtil {

	public String generateCode() {
		return UUID.randomUUID().toString();
	}
}
