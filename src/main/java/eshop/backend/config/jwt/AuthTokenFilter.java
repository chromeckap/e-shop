package eshop.backend.config.jwt;

import eshop.backend.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isAuthEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = getAuthHeader(request);
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtFromHeader(authHeader);
        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = loadUserDetails(userEmail);
            if (isValidToken(jwt, userDetails)) {
                authenticateUser(request, userDetails);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthEndpoint(HttpServletRequest request) {
        return request.getServletPath().contains("/api/auth");
    }

    private String getAuthHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    private String extractJwtFromHeader(String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }

    private UserDetails loadUserDetails(String userEmail) {
        return userDetailsService.loadUserByUsername(userEmail);
    }

    private boolean isValidToken(String jwt, UserDetails userDetails) {
        return jwtService.isTokenValid(jwt, userDetails) && isTokenNotExpiredOrRevoked(jwt);
    }

    private boolean isTokenNotExpiredOrRevoked(String jwt) {
        return tokenRepository.findByToken(jwt)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    private void authenticateUser(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}