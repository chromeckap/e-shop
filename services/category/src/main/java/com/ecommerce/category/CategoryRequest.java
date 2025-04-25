package com.ecommerce.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest (
        Long id,
        @NotBlank(message = "Jméno kategorie nesmí být prázdné.")
        @Size(max = 100, message = "Jméno kategorie nesmí být delší než 100 znaků.")
        String name,
        @Size(max = 500, message = "Popis kategorie nesmí být delší než 500 znaků.")
        String description,
        Long parentId
) {}
