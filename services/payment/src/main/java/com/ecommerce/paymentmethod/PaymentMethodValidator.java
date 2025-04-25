package com.ecommerce.paymentmethod;

import com.ecommerce.exception.PaymentMethodNotActiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentMethodValidator {

    public void validatePaymentMethodAccessible(PaymentMethod paymentMethod) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!paymentMethod.isActive() && !isAdmin)
            throw new PaymentMethodNotActiveException("Platební metoda není aktivní pro zákazníky.");
    }
}
