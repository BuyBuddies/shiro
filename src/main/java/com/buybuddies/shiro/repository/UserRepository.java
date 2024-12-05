package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByFirebaseUid(String firebaseUid);
}
