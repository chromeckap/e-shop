package com.ecommerce.userdetails;

import com.ecommerce.address.AddressResponse;
import lombok.Builder;

@Builder
public record UserDetailsResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String email,
        AddressResponse address
) {}
