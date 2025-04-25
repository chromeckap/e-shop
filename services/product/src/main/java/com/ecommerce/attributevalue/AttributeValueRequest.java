package com.ecommerce.attributevalue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AttributeValueRequest (
        Long id,
        @NotBlank(message = "Hodnota atributu nesmí být prázdný.")
        @Size(max = 100, message = "Hodnota atributu nesmí být delší než 100 znaků.")
        String value
) {}
