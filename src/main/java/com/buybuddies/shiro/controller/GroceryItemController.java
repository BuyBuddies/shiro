package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.GroceryItemDTO;
import com.buybuddies.shiro.entity.GroceryItem;
import com.buybuddies.shiro.enums.ItemCategory;
import com.buybuddies.shiro.service.GroceryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-items")
@RequiredArgsConstructor
public class GroceryItemController {
    private final GroceryItemService groceryItemService;

    @PostMapping
    public ResponseEntity<GroceryItemDTO> createGroceryItem(@RequestBody GroceryItemDTO groceryItemDTO) {
        return new ResponseEntity<>(groceryItemService.createGroceryItem(groceryItemDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroceryItemDTO> getGroceryItem(@PathVariable Long id) {
        return ResponseEntity.ok(groceryItemService.getGroceryItem(id));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<GroceryItemDTO> getGroceryItemByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(groceryItemService.getGroceryItemByBarcode(barcode));
    }

    @GetMapping
    public ResponseEntity<List<GroceryItemDTO>> getAllGroceryItems() {
        return ResponseEntity.ok(groceryItemService.getAllGroceryItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<GroceryItemDTO>> getGroceryItemsByCategory(
            @PathVariable ItemCategory category) {
        return ResponseEntity.ok(groceryItemService.getGroceryItemsByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GroceryItemDTO>> searchGroceryItems(@RequestParam String name) {
        return ResponseEntity.ok(groceryItemService.searchGroceryItems(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroceryItemDTO> updateGroceryItem(
            @PathVariable Long id,
            @RequestBody GroceryItemDTO groceryItemDTO) {
        return ResponseEntity.ok(groceryItemService.updateGroceryItem(id, groceryItemDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroceryItem(@PathVariable Long id) {
        groceryItemService.deleteGroceryItem(id);
        return ResponseEntity.noContent().build();
    }
}