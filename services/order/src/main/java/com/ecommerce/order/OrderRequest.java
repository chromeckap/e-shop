package com.ecommerce.order;

import com.ecommerce.feignclient.product.PurchaseRequest;
import com.ecommerce.userdetails.UserDetailsRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record OrderRequest(
        Long id,
        @NotNull(message = "Uživatel nesmí být prázdný.")
        UserDetailsRequest userDetails,
        @NotEmpty(message = "Zakoupené produkty nemohou být prázdné.")
        Set<@NotNull PurchaseRequest> products,
        @NotNull(message = "ID platební metody nesmí být nulové.")
        Long paymentMethodId,
        @NotNull(message = "ID metody doručení nesmí být nulové.")
        Long deliveryMethodId
) {}
