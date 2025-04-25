package com.ecommerce.deliverymethod;

import com.ecommerce.exception.DeliveryMethodNotActiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeliveryMethodValidator {

    public void validatedDeliveryMethodAccessible(DeliveryMethod deliveryMethod) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!deliveryMethod.isActive() && !isAdmin)
            throw new DeliveryMethodNotActiveException("Metoda pro doručení není aktivní pro zákazníky.");
    }
}
