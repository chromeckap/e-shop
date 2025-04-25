package com.ecommerce.deliverymethod;

import com.ecommerce.exception.DeliveryMethodNotActiveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryMethodValidatorTest {

    private DeliveryMethodValidator validator;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private DeliveryMethod deliveryMethod;

    @BeforeEach
    void setUp() {
        validator = new DeliveryMethodValidator();
    }

    @Test
    void testValidatedDeliveryMethodAccessible_ActiveMethod_AnyUser() {
        // Arrange
        when(deliveryMethod.isActive()).thenReturn(true);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertDoesNotThrow(() -> validator.validatedDeliveryMethodAccessible(deliveryMethod));
        }
    }

    @Test
    void testValidatedDeliveryMethodAccessible_InactiveMethod_AdminUser() {
        // Arrange
        when(deliveryMethod.isActive()).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Mock admin authority
            Collection<? extends GrantedAuthority> adminAuthorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
            doReturn(adminAuthorities).when(authentication).getAuthorities();

            // Act & Assert
            assertDoesNotThrow(() -> validator.validatedDeliveryMethodAccessible(deliveryMethod));
        }
    }

    @Test
    void testValidatedDeliveryMethodAccessible_InactiveMethod_NonAdminUser() {
        // Arrange
        when(deliveryMethod.isActive()).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Mock non-admin authority
            Collection<? extends GrantedAuthority> userAuthorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            doReturn(userAuthorities).when(authentication).getAuthorities();

            // Act & Assert
            assertThrows(DeliveryMethodNotActiveException.class,
                    () -> validator.validatedDeliveryMethodAccessible(deliveryMethod),
                    "Metoda pro doručení není aktivní pro zákazníky."
            );
        }
    }

    @Test
    void testValidatedDeliveryMethodAccessible_NullAuthentication() {
        // Arrange
        DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act & Assert
            assertThrows(NullPointerException.class,
                    () -> validator.validatedDeliveryMethodAccessible(deliveryMethod)
            );
        }
    }

    @Test
    void testComponentAnnotation() {
        // Verify that the class is annotated with @Component
        Component componentAnnotation = DeliveryMethodValidator.class.getAnnotation(Component.class);
        assertNotNull(componentAnnotation);
    }
}