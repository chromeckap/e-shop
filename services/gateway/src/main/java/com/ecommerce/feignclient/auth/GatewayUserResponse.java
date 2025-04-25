package com.ecommerce.feignclient.auth;

import java.util.Set;

public record GatewayUserResponse(
        Long id,
        String email,
        Set<String> roles
) {}
