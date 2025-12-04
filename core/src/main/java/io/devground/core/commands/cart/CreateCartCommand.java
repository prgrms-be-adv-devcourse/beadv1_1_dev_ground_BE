package io.devground.core.commands.cart;

import jakarta.validation.constraints.NotBlank;

public record CreateCartCommand(
	@NotBlank() String userCode
) {
}
