package io.devground.dbay.cart.domain.utils;

import java.util.UUID;

public final class DomainUtil {

    private DomainUtil() {}

    public static String generateCode() {
        return UUID.randomUUID().toString();
    }
}