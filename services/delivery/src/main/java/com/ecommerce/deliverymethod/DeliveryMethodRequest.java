package com.ecommerce.deliverymethod;

import com.ecommerce.strategy.CourierType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record DeliveryMethodRequest(
        Long id,
        @NotBlank(message = "Název metody dopravy nesmí být prázdný.")
        @NotEmpty(message = "Název metody dopravy nesmí být prázdný.")
        String name,
        @NotNull(message = "Typ kurýra nesmí být prázdný.")
        CourierType type,
        @NotNull(message = "Status zda je aktivní nesmí být prázdný.")
        boolean isActive,
        @NotNull(message = "Cena nesmí být prázdná.")
        @PositiveOrZero(message = "Cena musí být kladná nebo nulová.")
        BigDecimal price,
        boolean isFreeForOrderAbove,
        @PositiveOrZero(message = "Hodnota objednávky zdarma musí být kladná nebo nulová.")
        BigDecimal freeForOrderAbove
) {}