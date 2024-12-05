package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.GroceryListItem;
import com.buybuddies.shiro.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryListItemRepository extends JpaRepository<GroceryListItem, Long> {
    List<GroceryListItem> findByGroceryListId(Long groceryListId);
    List<GroceryListItem> findByGroceryListIdAndStatus(Long groceryListId, PurchaseStatus status);
}
