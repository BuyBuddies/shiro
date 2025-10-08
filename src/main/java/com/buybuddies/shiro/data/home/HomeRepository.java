package com.buybuddies.shiro.data.home;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
    List<Home> findByOwnerId(Long ownerId);
    List<Home> findByMembersId(Long userId);
    List<Home> findByOwnerFirebaseUid(String ownerFirebaseUid);
    List<Home> findByMembersFirebaseUid(String userFirebaseUid);
}