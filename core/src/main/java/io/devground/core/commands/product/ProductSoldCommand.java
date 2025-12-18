package io.devground.core.commands.product;

import java.util.List;

public record ProductSoldCommand(
        List<String> productCodes
) {
}
