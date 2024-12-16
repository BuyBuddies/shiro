package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.UserDTO;
import com.buybuddies.shiro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(
                userService.createUser(userDTO),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/firebase/{firebaseUid}")
    public ResponseEntity<UserDTO> getUserByFirebaseUid(
            @PathVariable String firebaseUid) {
        return ResponseEntity.ok(userService.getUserByFirebaseUid(firebaseUid));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Firebase-Uid") String firebaseUid) {
        return ResponseEntity.ok(userService.getUserByFirebaseUid(firebaseUid));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}