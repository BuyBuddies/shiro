package com.buybuddies.shiro.authorization;

import com.buybuddies.shiro.data.user.User;
import com.buybuddies.shiro.exception.FirebaseAuthenticationException;
import com.buybuddies.shiro.data.user.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final FirebaseAuth firebaseAuth;

    /**
     * Register a new user with Firebase authentication
     *
     * @param authHeader Authorization header with Bearer token
     * @param email User's email
     * @param name User's display name
     * @return Created user
     * @throws FirebaseAuthException if token verification fails
     */
    public User registerUser(String authHeader, String email, String name)
            throws FirebaseAuthException {
        log.debug("Registering user with email: {}", email);

        // 1. Extract and verify Firebase token
        String token = extractToken(authHeader);
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
        String firebaseUid = decodedToken.getUid();
        String firebaseEmail = decodedToken.getEmail();

        log.debug("Firebase token verified for UID: {}", firebaseUid);

        // 2. Security check: Email from token must match email in request
        if (!firebaseEmail.equals(email)) {
            log.warn("Email mismatch: token={}, request={}", firebaseEmail, email);
            throw new IllegalArgumentException("Email mismatch with Firebase token");
        }

        // 3. Check if user already exists
        if (userService.existsByFirebaseUid(firebaseUid)) {
            log.warn("User already exists with Firebase UID: {}", firebaseUid);
            throw new IllegalStateException("User already registered");
        }

        // 4. Create user in database
        return userService.createUser(firebaseUid, email, name);
    }

    /**
     * Check if a Firebase user exists in our database
     *
     * @param authHeader Authorization header with Bearer token
     * @return true if user exists, false otherwise
     * @throws FirebaseAuthException if token verification fails
     */
    public boolean checkUserExists(String authHeader) throws FirebaseAuthException {
        log.debug("Checking if user exists");

        FirebaseToken decodedToken = verifyAndDecodeToken(authHeader);
        boolean exists = userService.existsByFirebaseUid(decodedToken.getUid());

        log.debug("User exists check for UID {}: {}", decodedToken.getUid(), exists);
        return exists;
    }

    /**
     * Verify Firebase token and get user from database
     * Used by filter for authentication
     *
     * @param authHeader Authorization header with Bearer token
     * @return User from database
     * @throws FirebaseAuthException if token verification fails
     */
    public User authenticateAndGetUser(String authHeader) throws FirebaseAuthException {
        log.debug("Authenticating user");

        // Reuse the existing verification method
        FirebaseToken decodedToken = verifyAndDecodeToken(authHeader);
        User user = userService.getUserByFirebaseUid(decodedToken.getUid());

        if (user == null) {
            log.warn("User not found for Firebase UID: {}", decodedToken.getUid());
            throw new FirebaseAuthenticationException("User not found or no User record");
        }

        log.debug("User authenticated successfully: {}", user.getFirebaseUid());
        return user;
    }

    /**
     * Verify and decode Firebase token
     *
     * @param authHeader Authorization header with Bearer token
     * @return Decoded Firebase token
     * @throws FirebaseAuthException if token verification fails
     */
    public FirebaseToken verifyAndDecodeToken(String authHeader)
            throws FirebaseAuthException {
        try {
            String token = extractToken(authHeader);
            return firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            log.error("Failed to verify Firebase token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token verification: {}", e.getMessage());
            throw new FirebaseAuthenticationException("Token verification failed");
        }
    }

    /**
     * Extract token from "Bearer <token>" authorization header
     *
     * @param authHeader Authorization header
     * @return Extracted token
     * @throws FirebaseAuthenticationException if header format is invalid
     */
    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new FirebaseAuthenticationException("Invalid authorization header format");
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            throw new FirebaseAuthenticationException("Empty authentication token");
        }

        return token;
    }

    /**
     * Verify ownership of a resource
     * Checks if the current user owns the resource
     *
     * @param resourceOwnerId Firebase UID of resource owner
     * @param userFirebaseUid Firebase UID of current user
     * @throws FirebaseAuthenticationException if user doesn't own the resource
     */
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