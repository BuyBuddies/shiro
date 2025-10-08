package com.buybuddies.shiro.data.user;

import com.buybuddies.shiro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Firebase UID: " + firebaseUid));
    }


    /**
     * Create a new user
     * Called during Firebase registration
     *
     * @param firebaseUid Firebase UID from verified token
     * @param name User's display name
     * @param email User's email address
     * @return Created user entity
     */
    public User createUser(String firebaseUid, String email, String name) {
        log.info("Creating new user with email: {}", email);

        // Check if user already exists
        if (userRepository.existsByFirebaseUid(firebaseUid)) {
            throw new IllegalStateException("User already exists with this Firebase UID");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("User already exists with this email");
        }

        User user = userMapper.createUserEntity(firebaseUid, email, name);

        return userRepository.save(user);
    }


    /**
     * Update user profile
     * Only allows updating safe fields
     * @param firebaseUid Firebase UID from verified token
     * @param updates DTO with only safe fields to be updated
     * @return Updated user entity
     */
    public User updateUser(String firebaseUid, UpdateUserDTO updates) {
        log.debug("Updating user with firebaseUid: {}", firebaseUid);

        User user = getUserByFirebaseUid(firebaseUid);

        // Update only allowed fields
        if (updates.getName() != null) {
            user.setName(updates.getName());
        }

        // @PreUpdate from BaseEntity will automatically update updatedAt
        return userRepository.save(user);
    }

    /**
     * Delete user account
     * IMPORTANT: Client should also delete Firebase user
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);

        User user = getUserById(userId);

        // TODO: Add logic to delete user's related data (posts, comments, etc.)
        // TODO: Add deletion of firebase user
        // before deleting the user


        userRepository.delete(user);
    }

    /**
     * Check if user's data has changed since last sync
     * Used for efficient Android sync
     *
     * @param userId User's ID
     * @param lastSyncTimestamp Unix timestamp in seconds
     * @return true if data was modified after lastSyncTimestamp
     */
    @Transactional(readOnly = true)
    public boolean hasUserDataChanged(Long userId, Long lastSyncTimestamp) {
        User user = getUserById(userId);
        Instant lastSync = Instant.ofEpochSecond(lastSyncTimestamp);


        // TODO: Also check if user's related data changed
        // Example: boolean groceryItemUpdated = groceryItemRepository.existsByUserIdAndUpdatedAtAfter(userId, lastSync);

        // Check if user was updated after last sync
        return user.getUpdatedAt().isAfter(lastSync);
    }

    /**
     * Check if user exists by Firebase UID
     */
    @Transactional(readOnly = true)
    public boolean existsByFirebaseUid(String firebaseUid) {
        return userRepository.existsByFirebaseUid(firebaseUid);
    }
}
