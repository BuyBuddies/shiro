package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.GroceryItem;
import com.buybuddies.shiro.enums.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    List<GroceryItem> findByCategory(ItemCategory category);
    List<GroceryItem> findByNameContainingIgnoreCase(String name);
    Optional<GroceryItem> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);
}
