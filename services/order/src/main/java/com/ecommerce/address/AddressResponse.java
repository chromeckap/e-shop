package com.ecommerce.address;

import lombok.Builder;

@Builder
public record AddressResponse(
        Long id,
        String street,
        String city,
        String postalCode
) {}
