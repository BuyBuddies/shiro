package com.buybuddies.shiro.security;

import com.buybuddies.shiro.dto.UserDTO;
import com.buybuddies.shiro.exception.FirebaseAuthenticationException;
import com.buybuddies.shiro.service.UserService;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;

    public UserDTO authenticateAndGetUser(String authHeader) throws FirebaseAuthException {
        log.debug("Authenticating user");
        try {
            String token = extractToken(authHeader);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            UserDTO user = userService.getUserByFirebaseUid(decodedToken.getUid());

            if (user == null) {
                log.warn("User not found for Firebase UID: {}", decodedToken.getUid());
                throw new FirebaseAuthenticationException("User not found or no User record");
            }

            log.debug("User authenticated successfully: {}", user.getId());
            return user;
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication failed", e);
            throw e;
        }
    }

    public FirebaseToken verifyAndDecodeToken(String authHeader) throws FirebaseAuthException {
        try {
            String token = extractToken(authHeader);
            return FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            log.error("Failed to verify Firebase token: {}", e.getMessage());
            throw e;
        }catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }
    }


    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new FirebaseAuthenticationException("Invalid authorization header format");
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            throw new FirebaseAuthenticationException("Empty authentication token");
        }
        return token;
    }

    public void verifyOwnership(String resourceOwnerId, String userFirebaseUid) {
        if (!resourceOwnerId.equals(userFirebaseUid)) {
            log.warn("User {} not authorized to access resource owned by {}",
                    userFirebaseUid, resourceOwnerId);
            throw new FirebaseAuthenticationException(
                    "User not authorized for this operation"
            );
        }
    }
}