package io.devground.core.commands.cart;

import java.util.List;

public record DeleteCartItemsCommand(
	String userCode,
	List<String> productCodes
) {
}
