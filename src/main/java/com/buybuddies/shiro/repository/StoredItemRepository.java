package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.StoredItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StoredItemRepository extends JpaRepository<StoredItem, Long> {
    List<StoredItem> findByDepotId(Long depotId);
    List<StoredItem> findByGroceryItemId(Long groceryItemId);
    List<StoredItem> findByDepotIdAndGroceryItemId(Long depotId, Long groceryItemId);
    List<StoredItem> findByDepotIdAndGroceryItemNameIgnoreCase(Long depotId, String groceryItemName);
    List<StoredItem> findByExpirationDateBefore(LocalDateTime date);
    List<StoredItem> findByDepotIdAndExpirationDateBefore(Long depotId, LocalDateTime date);
}
