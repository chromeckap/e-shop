package com.ecommerce.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "Email nesmí být prázdný.")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                message = "Neplatný formát emailu."
        )
        String email,

        @NotBlank(message = "Heslo nesmí být prázdné.")
        String password
) {}
