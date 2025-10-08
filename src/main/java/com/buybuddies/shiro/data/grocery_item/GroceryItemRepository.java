package com.buybuddies.shiro.data.grocery_item;

import com.buybuddies.shiro.data.enums.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    List<GroceryItem> findByCategory(ItemCategory category);
    Optional<GroceryItem> findByNameContainingIgnoreCase(String name);
    Optional<GroceryItem> findByName(String name);
    Optional<GroceryItem> findByNameIgnoreCase(String name);

    Optional<GroceryItem> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);
}
