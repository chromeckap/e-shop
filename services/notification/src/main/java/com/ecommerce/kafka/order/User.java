package com.ecommerce.kafka.order;

public record User(
        Long id,
        String firstName,
        String lastName,
        String email
) {}
