package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.GroceryListItem;
import com.buybuddies.shiro.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroceryListItemRepository extends JpaRepository<GroceryListItem, Long> {
    List<GroceryListItem> findByGroceryListId(Long groceryListId);
    List<GroceryListItem> findByGroceryListIdAndStatus(Long groceryListId, PurchaseStatus status);

    @Query("SELECT gli FROM GroceryListItem gli " +
            "JOIN gli.groceryList gl " +
            "LEFT JOIN gl.members m " +
            "WHERE gl.owner.firebaseUid = :userId " +
            "OR m.firebaseUid = :userId")
    List<GroceryListItem> findByOwnerIdOrMemberId(String userId);
}
