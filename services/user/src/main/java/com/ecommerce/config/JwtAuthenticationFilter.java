package com.ecommerce.config;

import com.ecommerce.token.TokenMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenMapper tokenMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            if (request.getServletPath().contains("api/v1/auth/register")
                    || request.getServletPath().contains("api/v1/auth/login")
            ) {
                filterChain.doFilter(request, response);
                return;
            }

            String accessToken = jwtService.extractTokenFromCookies(request, "access_token");
            if (accessToken == null || accessToken.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(accessToken);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(accessToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            ResponseCookie accessTokenCookie = tokenMapper.discardAccessCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

            ResponseCookie refreshTokenCookie = tokenMapper.discardRefreshCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalidní token.");
        }

    }
}
