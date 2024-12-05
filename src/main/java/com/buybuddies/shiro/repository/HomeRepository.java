package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
    List<Home> findByOwnerId(Long ownerId);
    List<Home> findByMembersId(Long userId);
    List<Home> findByOwnerFirebaseUid(String ownerFirebaseUid);
    List<Home> findByMembersFirebaseUid(String userFirebaseUid);
}