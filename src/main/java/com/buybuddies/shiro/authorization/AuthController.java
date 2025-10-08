package com.buybuddies.shiro.authorization;


import com.buybuddies.shiro.data.user.User;
import com.buybuddies.shiro.data.user.UserDTO;
import com.buybuddies.shiro.data.user.UserMapper;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for user registration and login
 * These endpoints do NOT require authentication
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    /**
     * Register a new user after Firebase authentication
     *
     * @param authHeader Firebase ID token (Bearer token)
     * @param request User registration data
     * @return Created user data
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RegisterRequestDTO request
    ) throws FirebaseAuthException {
        log.info("Registration attempt for email: {}", request.getEmail());

        User user = authService.registerUser(authHeader, request.getEmail(), request.getName());

        log.info("User created successfully: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
    }

    /**
     * Check if current Firebase user exists in our database
     *
     * @param authHeader Firebase ID token
     * @return {"exists": true/false}
     */
    @GetMapping("/check")
    public ResponseEntity<UserExistsResponse> checkUserExists(
            @RequestHeader("Authorization") String authHeader
    ) throws FirebaseAuthException {
        log.debug("Checking if user exists");

        boolean exists = authService.checkUserExists(authHeader);

        log.debug("User exists check result: {}", exists);
        return ResponseEntity.ok(new UserExistsResponse(exists));
    }

    /**
     * Response DTO for user existence check
     */
    public record UserExistsResponse(boolean exists) {}
}