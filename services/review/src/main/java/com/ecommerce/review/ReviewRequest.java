package com.ecommerce.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        Long id,
        @Min(value = 1, message = "Hodnocení musí být mezi 1 a 5.")
        @Max(value = 5, message = "Hodnocení musí být mezi 1 a 5.")
        int rating,
        @NotNull(message = "Text recenze nesmí být prázdný.")
        @NotEmpty(message = "Text recenze nesmí být prázdný.")
        String text,
        @NotNull(message = "ID uživatele nesmí být prázdné.")
        Long userId,
        @NotNull(message = "ID produktu nesmí být prázdné.")
        Long productId
) {}
