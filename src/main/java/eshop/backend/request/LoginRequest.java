package eshop.backend.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public record LoginRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank String password
) {}