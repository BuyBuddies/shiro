package com.buybuddies.shiro.data.grocery_item;

import com.buybuddies.shiro.data.enums.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    Optional<GroceryItem> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);

    List<GroceryItem> findByCategory(ItemCategory category);

    @Query("SELECT g FROM GroceryItem g WHERE " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<GroceryItem> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    @Query("SELECT g FROM GroceryItem g WHERE g.category = :category")
    List<GroceryItem> findByCategoryOptimized(@Param("category") ItemCategory category);

    Optional<GroceryItem> findByNameContainingIgnoreCase(String name);

    Optional<GroceryItem> findByNameIgnoreCase(String name);
}
