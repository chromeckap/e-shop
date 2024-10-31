package eshop.backend.request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
