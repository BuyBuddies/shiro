package com.buybuddies.shiro.data.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirebaseUid(String firebaseUid);
    boolean existsByFirebaseUid(String firebaseUid);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
