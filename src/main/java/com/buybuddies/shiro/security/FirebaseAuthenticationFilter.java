package com.buybuddies.shiro.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        log.debug("Received authorization header: {}",
                authHeader != null ? (authHeader.substring(0, Math.min(authHeader.length(), 15)) + "...") : "null");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader;
                FirebaseToken decodedToken = authService.verifyAndDecodeToken(token);
                log.debug("Successfully decoded token for user: {}", decodedToken.getUid());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        decodedToken.getUid(),
                        null,
                        List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("firebaseUid", decodedToken.getUid());
            }
        } catch (FirebaseAuthException e) {
            log.error("Authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed: " + e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error during authentication");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/h2-console") || path.contains("/error");
    }
}
