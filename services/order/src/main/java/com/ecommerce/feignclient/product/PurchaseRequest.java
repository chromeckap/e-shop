package com.ecommerce.feignclient.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseRequest(
        @NotNull(message = "Produktové ID nesmí být nulové.")
        Long id,
        @Min(value = 1, message = "Množství musí být alespoň 1.")
        int quantity
) {}
