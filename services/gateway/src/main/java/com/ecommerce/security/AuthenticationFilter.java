package com.ecommerce.security;

import com.ecommerce.feignclient.auth.GatewayUserResponse;
import com.ecommerce.feignclient.auth.AuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final AuthClient authClient;

    /**
     * Constructor for the AuthenticationFilter.
     *
     * @param authClient The UserClient used to validate the user's authentication.
     */
    public AuthenticationFilter(@Lazy AuthClient authClient) {
        super(Config.class);
        this.authClient = authClient;
    }

    /**
     * Applies the authentication filter to the incoming request.
     * It checks for cookies, validates the user, and modifies the request headers
     * if the user is successfully authenticated.
     *
     * @param config Configuration for the filter (currently not used, but can be extended).
     * @return The GatewayFilter that processes the request and applies authentication.
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<String> cookies = request.getHeaders().getOrDefault("Cookie", Collections.emptyList());
            String cookieHeader = cookies.isEmpty() ? "" : String.join("; ", cookies);

            log.info("Forwarding cookies: {}", cookieHeader);

            GatewayUserResponse userResponse = null;
            
            if (!cookieHeader.isEmpty())
                userResponse = authClient.validateRequest(cookieHeader);

            if (userResponse == null || userResponse.email() == null)
                return chain.filter(exchange.mutate().build());

            log.info("User validated successfully: {}", userResponse);

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-UserId", String.valueOf(userResponse.id()))
                    .header("X-User-Username", userResponse.email())
                    .header("X-User-Roles", String.join(",", userResponse.roles()))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    public static class Config {
    }
}