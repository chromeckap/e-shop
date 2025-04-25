package com.ecommerce.paymentmethod;

import com.ecommerce.exception.PaymentMethodNotActiveException;
import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodValidatorTest {

    @InjectMocks
    private PaymentMethodValidator paymentMethodValidator;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private PaymentMethod activePaymentMethod;
    private PaymentMethod inactivePaymentMethod;

    @BeforeEach
    void setUp() {
        // Create an active payment method
        activePaymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("Active Payment Method")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .build();

        // Create an inactive payment method
        inactivePaymentMethod = PaymentMethod.builder()
                .id(2L)
                .name("Inactive Payment Method")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(false)
                .price(new BigDecimal("5.99"))
                .build();
    }

    @Test
    void validatePaymentMethodAccessible_WhenMethodIsActive_ShouldNotThrowException() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Set up regular user role
            Collection<? extends GrantedAuthority> adminAuthorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            doReturn(adminAuthorities).when(authentication).getAuthorities();

            // Act & Assert
            assertDoesNotThrow(() -> paymentMethodValidator.validatePaymentMethodAccessible(activePaymentMethod));

            // Verify
            verify(authentication).getAuthorities();
        }
    }

    @Test
    void validatePaymentMethodAccessible_WhenMethodIsInactiveAndUserIsNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Set up regular user role
            Collection<? extends GrantedAuthority> adminAuthorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            doReturn(adminAuthorities).when(authentication).getAuthorities();

            // Act & Assert
            PaymentMethodNotActiveException exception = assertThrows(
                    PaymentMethodNotActiveException.class,
                    () -> paymentMethodValidator.validatePaymentMethodAccessible(inactivePaymentMethod)
            );

            // Verify exception message
            assert(exception.getMessage().contains("Platební metoda není aktivní pro zákazníky"));

            // Verify
            verify(authentication).getAuthorities();
        }
    }

    @Test
    void validatePaymentMethodAccessible_WhenMethodIsInactiveAndUserIsAdmin_ShouldNotThrowException() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Set up admin role
            Collection<? extends GrantedAuthority> adminAuthorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
            doReturn(adminAuthorities).when(authentication).getAuthorities();

            // Act & Assert
            assertDoesNotThrow(() -> paymentMethodValidator.validatePaymentMethodAccessible(inactivePaymentMethod));

            // Verify
            verify(authentication).getAuthorities();
        }
    }
}