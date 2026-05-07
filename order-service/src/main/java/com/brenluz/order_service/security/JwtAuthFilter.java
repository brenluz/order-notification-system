package com.brenluz.order_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Filter running for: " + request.getRequestURI());
        System.out.println("Auth header: " + request.getHeader("Authorization"));

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (jwtUtil.isTokenValid(token)) {
            System.out.println("Token valid for: " + jwtUtil.extractEmail(token));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    jwtUtil.extractEmail(token), null, List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        else {
            System.out.println("Token invalid");
        }
        filterChain.doFilter(request, response);
    }
}
