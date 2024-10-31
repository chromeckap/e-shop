package eshop.backend.request;

public record RegisterRequest(
        String email,
        String password,
        String confirmationPassword,
        String firstName,
        String lastName
) {}
