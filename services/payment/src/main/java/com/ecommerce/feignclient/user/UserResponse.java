package com.ecommerce.feignclient.user;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {}