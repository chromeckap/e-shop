package com.ecommerce.authentication;

import lombok.Builder;

import java.util.Set;

@Builder
public record GatewayUserResponse(
        Long id,
        String email,
        Set<String> roles
) {}
