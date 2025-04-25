package com.ecommerce.variant.purchase;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message = "Produktové ID nesmí být nulové.")
        Long id,
        @Min(value = 1, message = "Množství musí být alespoň 1.")
        @Max(value = 50, message = "Množství nesmí přesáhnout 50.")
        int quantity
) {}
