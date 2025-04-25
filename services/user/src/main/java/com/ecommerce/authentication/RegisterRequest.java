package com.ecommerce.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Jméno nesmí být prázdné.")
        @Size(min = 2, max = 50, message = "Jméno musí mít 2 až 50 znaků.")
        String firstName,
        @NotBlank(message = "Příjmení nesmí být prázdné.")
        @Size(min = 2, max = 50, message = "Příjmení musí mít 2 až 50 znaků.")
        String lastName,
        @NotBlank(message = "Email nesmí být prázdný.")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                message = "Neplatný formát emailu."
        )        String email,
        @NotBlank(message = "Heslo nesmí být prázdné.")
        @Size(min = 6, max = 255, message = "Heslo musí mít 6 až 255 znaků.")
        String password,
        @NotBlank(message = "Potvrzení hesla nesmí být prázdné.")
        @Size(min = 6, max = 255, message = "Potvrení hesla musí mít 6 až 255 znaků.")
        String confirmPassword
) {}