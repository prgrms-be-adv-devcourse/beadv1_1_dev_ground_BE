package io.devground.core.commands.cart;

import java.util.List;

public record CompleteCartCommand(
	String cartCode,
	List<String> productCodes
) {
}
