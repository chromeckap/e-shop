package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValueRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AttributeRequest (
        Long id,
        @NotBlank(message = "Název atributu nesmí být prázdný.")
        @Size(max = 100, message = "Název atributu nesmí být delší než 100 znaků.")
        String name,
        @NotNull(message = "Seznam hodnot atributu nesmí být prázdný.")
        List<AttributeValueRequest> values
) {}