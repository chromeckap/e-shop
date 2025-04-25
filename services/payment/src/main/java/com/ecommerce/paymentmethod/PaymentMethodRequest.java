package com.ecommerce.paymentmethod;

import com.ecommerce.strategy.PaymentGatewayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PaymentMethodRequest(
        Long id,
        @NotBlank(message = "Název platební metody nesmí být prázdný.")
        @NotEmpty(message = "Název platební metody nesmí být prázdný.")
        String name,
        @NotNull(message = "Typ nesmí být prázdný.")
        PaymentGatewayType type,
        @NotNull(message = "Aktivita musí být zadána.")
        boolean isActive,
        @NotNull(message = "Cena nesmí být prázdná.")
        @PositiveOrZero(message = "Cena musí být kladná nebo nulová.")
        BigDecimal price,
        boolean isFreeForOrderAbove,
        @PositiveOrZero(message = "Limit pro platební metodu musí být kladný nebo nulový.")
        BigDecimal freeForOrderAbove
) {}