package com.ecommerce.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Ulice nesmí být prázdná")
        @Size(max = 255, message = "Ulice může mít maximálně 255 znaků")
        String street,

        @NotBlank(message = "Město nesmí být prázdné")
        @Size(max = 100, message = "Město může mít maximálně 100 znaků")
        String city,

        @NotBlank(message = "PSČ nesmí být prázdné")
        @Pattern(regexp = "\\d{3} ?\\d{2}", message = "PSČ musí být ve formátu 12345 nebo 123 45")
        String postalCode
) {}
