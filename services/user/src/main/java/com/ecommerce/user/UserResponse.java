package com.ecommerce.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        LocalDateTime createTime
) {}
