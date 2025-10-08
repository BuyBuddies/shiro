package com.buybuddies.shiro.data.user;

import com.buybuddies.shiro.authorization.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Get current user's profile
     *
     * @return Current user's data
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser (
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Fetching profile for user: {}", currentUser.getEmail());
        User user = userService.getUserByFirebaseUid(currentUser.getFirebaseUid());
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    /**
     * Update current user's profile
     * @return Updated user's data
     */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser (
            @Valid @RequestBody UpdateUserDTO updates,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Updating profile for user: {}", currentUser.getEmail());
        User user = userService.updateUser(currentUser.getFirebaseUid(), updates);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    /**
     * Delete current user's account
     * TODO: IMPORTANT: This should also delete the Firebase user on the client side
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Deleting account for user: {}", currentUser.getEmail());
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if user's data has been modified since last sync
     * Used for: Efficient sync - only fetch if data changed
     *
     * @param lastSyncTime Unix timestamp (seconds) of last sync
     * @return true if data was modified after lastSyncTime, false otherwise
     */
    @GetMapping("/me/sync-status")
    public ResponseEntity<SyncStatusResponse> checkSyncStatus(
            @RequestParam Long lastSyncTime,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Checking sync status for user: {}", currentUser.getEmail());
        boolean needsSync = userService.hasUserDataChanged(
                currentUser.getId(),
                lastSyncTime
        );
        return ResponseEntity.ok(new SyncStatusResponse(needsSync));
    }

    /**
     * Response DTO for sync status check
     */
    public record SyncStatusResponse(boolean needsSync) {}
}

/*
*
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserData(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        log.info("Retrieving user data for user UID: {}", authenticatedUser.getFirebaseUid());

        User userFromDB = userService.getUserByFirebaseUid(authenticatedUser.getFirebaseUid());

        return ResponseEntity.ok(userMapper.toDTO(userFromDB));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody @Validated UserUpdateDTO updateDTO
    ) {
        log.info("Updating user data for user UID: {}", authenticatedUser.getFirebaseUid());

        User updatedUser = userService.updateUser(authenticatedUser.getFirebaseUid(), updateDTO);
        UserDTO dto = userMapper.toDTO(updatedUser);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            HttpServletRequest request,
            @RequestBody UserDTO userDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Received delete user request for Firebase UID: {}", userDTO.getFirebaseUid());

        if (!firebaseUid.equals(userDTO.getFirebaseUid())) {
            log.warn("User {} attempted to delete different user {}",
                    firebaseUid, userDTO.getFirebaseUid());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (userService.getUserByFirebaseUid(firebaseUid) == null) {
            log.warn("User not found for deletion: {}", firebaseUid);
            return ResponseEntity.notFound().build();
        }

        log.info("Deleting user with Firebase UID: {}", firebaseUid);
        userService.deleteUserByFirebaseUid(firebaseUid);
        log.info("User deleted successfully");
        return ResponseEntity.noContent().build();
    }
}
 */
//    @GetMapping("/me")
//    public ResponseEntity<UserDTO> getUser(
//            HttpServletRequest request
//    ) {
//        String requestFirebaseUid = (String) request.getAttribute("firebaseUid");
//        log.info("Received get user request for Firebase UID: {}", requestFirebaseUid);
//
//        try {
//            UserDTO user = userService.getUserByFirebaseUid(requestFirebaseUid);
//            if (user == null) {
//                log.info("User not found for Firebase UID: {}", requestFirebaseUid);
//                return ResponseEntity.notFound().build();
//            }
//
//            log.info("Successfully retrieved user with Firebase UID: {}", requestFirebaseUid);
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            log.error("Error retrieving user", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }



//    @PostMapping("/create_update")
//    public ResponseEntity<UserDTO> createOrUpdateUser(
//            HttpServletRequest request,
//            @Valid @RequestBody UserDTO userDTO
//    ) {
//        log.info("Received create or update user request for email: {}", userDTO.getEmail());
//
//        String firebaseUid = (String) request.getAttribute("firebaseUid");
//
//        if (!firebaseUid.equals(userDTO.getFirebaseUid())) {
//            log.warn("Firebase UID mismatch. Token: {}, DTO: {}", firebaseUid, userDTO.getFirebaseUid());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        try {
//            User existingUser = userService.getUserByFirebaseUid(firebaseUid);
//            if (existingUser != null) {
//                log.info("User found, with Firebase UID: {}", firebaseUid);
//                log.info("User found with email: {}", existingUser.getEmail());
//                return ResponseEntity.ok(userMapper.toDTO(existingUser));
//            }
//
//            log.info("User not found, creating new user with Firebase UID: {}", firebaseUid);
//            return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
//        } catch (Exception e) {
//            log.error("Error processing user", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }