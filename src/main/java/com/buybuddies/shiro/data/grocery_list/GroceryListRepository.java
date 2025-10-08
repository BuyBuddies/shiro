package com.buybuddies.shiro.data.grocery_list;

import com.buybuddies.shiro.data.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {
    @Query("SELECT gl FROM GroceryList gl WHERE gl.owner.firebaseUid = :firebaseUid OR :firebaseUid IN (SELECT m.firebaseUid FROM gl.members m)")
    List<GroceryList> findByOwnerOrMemberFirebaseUid(String firebaseUid);

    List<GroceryList> findByOwnerFirebaseUid(String ownerFirebaseUid);

    Optional<GroceryList> findByNameAndOwner(String name, User owner);
    void deleteByNameAndOwner(String name, User owner);
    Optional<GroceryList> findByOwnerFirebaseUidAndName(String ownerFirebaseUid, String name);
}