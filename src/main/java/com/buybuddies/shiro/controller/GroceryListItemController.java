package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.GroceryListItemDTO;
import com.buybuddies.shiro.entity.GroceryListItem;
import com.buybuddies.shiro.enums.PurchaseStatus;
import com.buybuddies.shiro.service.GroceryListItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-list-items")
@RequiredArgsConstructor
public class GroceryListItemController {
    private final GroceryListItemService groceryListItemService;

    @PostMapping
    public ResponseEntity<GroceryListItemDTO> createListItem(@RequestBody GroceryListItemDTO listItemDTO) {
        return new ResponseEntity<>(groceryListItemService.createListItem(listItemDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroceryListItemDTO> getListItem(@PathVariable Long id) {
        return ResponseEntity.ok(groceryListItemService.getListItem(id));
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<GroceryListItemDTO>> getItemsByList(@PathVariable Long listId) {
        return ResponseEntity.ok(groceryListItemService.getItemsByList(listId));
    }

    @GetMapping("/list/{listId}/status/{status}")
    public ResponseEntity<List<GroceryListItemDTO>> getItemsByListAndStatus(
            @PathVariable Long listId,
            @PathVariable PurchaseStatus status) {
        return ResponseEntity.ok(groceryListItemService.getItemsByListAndStatus(listId, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroceryListItemDTO> updateListItem(
            @PathVariable Long id,
            @RequestBody GroceryListItemDTO listItemDTO) {
        return ResponseEntity.ok(groceryListItemService.updateListItem(id, listItemDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GroceryListItemDTO> updateItemStatus(
            @PathVariable Long id,
            @RequestBody PurchaseStatus status) {
        return ResponseEntity.ok(groceryListItemService.updateItemStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListItem(@PathVariable Long id) {
        groceryListItemService.deleteListItem(id);
        return ResponseEntity.noContent().build();
    }
}