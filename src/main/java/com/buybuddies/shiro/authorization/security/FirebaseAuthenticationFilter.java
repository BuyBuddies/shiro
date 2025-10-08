package com.buybuddies.shiro.authorization.security;

import com.buybuddies.shiro.authorization.AuthService;
import com.buybuddies.shiro.data.user.User;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that intercepts incoming HTTP requests to validate a Firebase ID Token from the 'Authorization' header.
 * If the token is valid, it populates the Spring SecurityContext with a fully authenticated
 * user object, including their roles (authorities), fetched from the local database.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    /**
     * Main filtering logic. Validates the 'Authorization: Bearer <token>' header
     * and sets the user's authentication context if the token is valid.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            log.debug("Authorization header found, attempting to verify token.");
            User user = authService.authenticateAndGetUser(authHeader);
            log.debug("User authenticated: {}", user.getEmail());

            CustomUserDetails userDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (FirebaseAuthException e) {
            log.warn("Firebase authentication failed: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication failed");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }

    /**
     * Specifies paths that should not be processed by this filter.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console") || path.startsWith("/error");
    }
}
