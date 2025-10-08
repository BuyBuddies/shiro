package com.buybuddies.shiro.data.user;

import org.springframework.stereotype.Component;


/**
 * Mapper between User entity and DTOs
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to DTO for API response
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());

        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    /**
     * Create User entity from registration data
     * Used during user registration
     *
     * @param firebaseUid Firebase UID from verified token
     * @param email User's email address
     * @param name User's display name
     * @return User entity ready to save
     */
    public User createUserEntity(String firebaseUid, String email, String name) {
        User user = new User();
        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);
        user.setName(name);
        user.setRole(Role.USER);

        // createdAt and updatedAt set automatically by @PrePersist

        return user;
    }
}