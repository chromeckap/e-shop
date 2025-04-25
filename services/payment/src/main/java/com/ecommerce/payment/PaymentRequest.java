package com.ecommerce.payment;

import com.ecommerce.feignclient.user.UserResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        Long id,
        @NotNull(message = "ID objednávky nesmí být prázdné.")
        Long orderId,
        @NotNull(message = "Částka platby nesmí být prázdná.")
        @Positive(message = "Částka platby musí být kladná.")
        BigDecimal totalPrice,
        @NotNull(message = "Typ platební metody nesmí být prázdný.")
        Long paymentMethodId,
        @NotNull(message = "Odpověď uživatele nesmí být prázdná.")
        UserResponse user,
        String OrderCreateTime
) {}
