package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.UserDTO;
import com.buybuddies.shiro.security.AuthenticationService;
import com.buybuddies.shiro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthenticationService authService;

    @PostMapping("/create_update")
    public ResponseEntity<UserDTO> createOrUpdateUser(
            HttpServletRequest request,
            @RequestBody UserDTO userDTO
    ) {
        log.info("Received create or update user request for email: {}", userDTO.getEmail());

        String firebaseUid = (String) request.getAttribute("firebaseUid");

        if (!firebaseUid.equals(userDTO.getFirebaseUid())) {
            log.warn("Firebase UID mismatch. Token: {}, DTO: {}", firebaseUid, userDTO.getFirebaseUid());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UserDTO existingUser = userService.getUserByFirebaseUid(firebaseUid);
            if (existingUser != null) {
                log.info("User found, with Firebase UID: {}", firebaseUid);
                return ResponseEntity.ok(existingUser);
            }

            log.info("User not found, creating new user with Firebase UID: {}", firebaseUid);
            return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error processing user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(
            HttpServletRequest request,
            @RequestBody UserDTO userDTO
    ) {
        log.info("Received create user request for email: {}", userDTO.getEmail());

        String firebaseUid = (String) request.getAttribute("firebaseUid");

        if (!firebaseUid.equals(userDTO.getFirebaseUid())) {
            log.warn("Firebase UID mismatch. Token: {}, DTO: {}", firebaseUid, userDTO.getFirebaseUid());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            log.info("Creating new user in database");
            UserDTO createdUser = userService.createUser(userDTO);
            log.info("User created successfully with ID: {}", createdUser.getId());
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(
            HttpServletRequest request,
            @RequestBody UserDTO userDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Received update user request for Firebase UID: {}", userDTO.getFirebaseUid());

        if (!firebaseUid.equals(userDTO.getFirebaseUid())) {
            log.warn("User {} attempted to update different user {}",
                    firebaseUid, userDTO.getFirebaseUid());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDTO existingUser = userService.getUserByFirebaseUid(firebaseUid);
        if (existingUser == null) {
            log.warn("User not found with Firebase UID: {}", firebaseUid);
            return ResponseEntity.notFound().build();
        }

        log.info("Updating user data");
        userDTO.setFirebaseUid(firebaseUid); // Ensure Firebase UID doesn't change
        UserDTO updatedUser = userService.updateUser(firebaseUid, userDTO);
        log.info("User updated successfully");
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete")
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

        UserDTO existingUser = userService.getUserByFirebaseUid(firebaseUid);
        if (existingUser == null) {
            log.warn("User not found for deletion: {}", firebaseUid);
            return ResponseEntity.notFound().build();
        }

        log.info("Deleting user with Firebase UID: {}", firebaseUid);
        userService.deleteUserByFirebaseUid(firebaseUid);
        log.info("User deleted successfully");
        return ResponseEntity.noContent().build();
    }
}