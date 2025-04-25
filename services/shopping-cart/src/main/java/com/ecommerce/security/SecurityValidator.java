package com.ecommerce.security;

import com.ecommerce.exception.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityValidator {
    public void validateUserAccess(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new UnauthorizedAccessException("Uživatel není autentizován.");

        Long authenticatedUserId;
        try {
            authenticatedUserId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new UnauthorizedAccessException("Neplatný formát ID uživatele.");
        }

        if (!userId.equals(authenticatedUserId))
            throw new UnauthorizedAccessException("Uživatel může přistupovat pouze k vlastnímu košíku.");
    }
}