package com.ecommerce.security;

import com.ecommerce.feignclient.auth.GatewayUserResponse;
import com.ecommerce.feignclient.auth.AuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest originalRequest;

    @Mock
    private ServerHttpRequest modifiedRequest;

    @Mock
    private GatewayFilterChain chain;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        authenticationFilter = new AuthenticationFilter(authClient);
    }

    @Test
    void testComponentAnnotation() {
        // Verify that the class is annotated with @Component
        Component componentAnnotation = AuthenticationFilter.class.getAnnotation(Component.class);
        assertNotNull(componentAnnotation);
    }

    @Test
    void testApply_NoCookies() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        when(exchange.getRequest()).thenReturn(originalRequest);
        when(originalRequest.getHeaders()).thenReturn(headers);

        ServerWebExchange.Builder mockBuilder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(exchange);

        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to ensure the operation completes
        verify(chain).filter(any());
        verify(authClient, never()).validateRequest(any());
    }

    @Test
    void testApply_ValidUser() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.put("Cookie", Collections.singletonList("session=test-cookie"));

        GatewayUserResponse userResponse = new GatewayUserResponse(
                1L,
                "test@example.com",
                Set.of("ROLE_USER")
        );

        // Setup mocking for reactive chain
        when(exchange.getRequest()).thenReturn(originalRequest);
        when(originalRequest.getHeaders()).thenReturn(headers);

        ServerWebExchange.Builder mockExchangeBuilder = mock(ServerWebExchange.Builder.class);
        ServerHttpRequest.Builder mockRequestBuilder = mock(ServerHttpRequest.Builder.class);

        // Mock request mutation
        when(originalRequest.mutate()).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.header(anyString(), any())).thenReturn(mockRequestBuilder); // 👈 důležité
        when(mockRequestBuilder.build()).thenReturn(modifiedRequest);

        // Mock exchange mutate
        when(exchange.mutate()).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.request(modifiedRequest)).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.build()).thenReturn(exchange);

        // Mock user validation
        when(authClient.validateRequest("session=test-cookie")).thenReturn(userResponse);

        // Mock filter chain
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to ensure the operation completes
        verify(authClient).validateRequest("session=test-cookie");
        verify(chain).filter(any());
    }


    @Test
    void testApply_InvalidUser() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.put("Cookie", Collections.singletonList("session=invalid-cookie"));

        when(exchange.getRequest()).thenReturn(originalRequest);
        when(originalRequest.getHeaders()).thenReturn(headers);

        ServerWebExchange.Builder mockBuilder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(exchange);

        // Mock user validation
        when(authClient.validateRequest("session=invalid-cookie")).thenReturn(null);

        // Mock filter chain
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to ensure the operation completes
        verify(authClient).validateRequest("session=invalid-cookie");
        verify(chain).filter(any());
    }

    @Test
    void testApply_UserWithMultipleCookies() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.put("Cookie", Collections.singletonList("session=test-cookie; remember-me=another-cookie"));

        GatewayUserResponse userResponse = new GatewayUserResponse(
                1L,
                "test@example.com",
                Set.of("ROLE_USER", "ROLE_ADMIN")
        );

        // Setup mocking for reactive chain
        when(exchange.getRequest()).thenReturn(originalRequest);
        when(originalRequest.getHeaders()).thenReturn(headers);

        // Mocking the request builder
        ServerHttpRequest.Builder mockRequestBuilder = mock(ServerHttpRequest.Builder.class);
        ServerHttpRequest modifiedRequest = mock(ServerHttpRequest.class); // result of builder.build()

        when(originalRequest.mutate()).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.header(anyString(), any())).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.build()).thenReturn(modifiedRequest);

        // Mock exchange mutate
        ServerWebExchange.Builder mockExchangeBuilder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.request(modifiedRequest)).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.build()).thenReturn(exchange);

        // Mock user validation
        when(authClient.validateRequest("session=test-cookie; remember-me=another-cookie")).thenReturn(userResponse);

        // Mock filter chain
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to ensure the operation completes
        verify(authClient).validateRequest("session=test-cookie; remember-me=another-cookie");
        verify(chain).filter(any());
    }


    @Test
    void testConfigClass() {
        // Verify the Config inner class can be instantiated
        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        assertNotNull(config);
    }
}