package com.ecommerce.userdetails;

import com.ecommerce.address.AddressRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDetailsRequest(
        @NotNull(message = "ID uživatele musí být zadáno.")
        Long id,
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
        @NotNull(message = "Adresa nesmí být prázdná.")
        AddressRequest address
) {}
