package com.buybuddies.shiro.data.depot_item;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stored-items")
@RequiredArgsConstructor
public class StoredItemController {
    private final StoredItemService storedItemService;

    @PostMapping
    public ResponseEntity<StoredItemDTO> createStoredItem(@RequestBody StoredItemDTO storedItemDTO) {
        return new ResponseEntity<>(storedItemService.createStoredItem(storedItemDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoredItemDTO> getStoredItem(@PathVariable Long id) {
        return ResponseEntity.ok(storedItemService.getStoredItem(id));
    }

    @GetMapping("/depot/{depotId}")
    public ResponseEntity<List<StoredItemDTO>> getItemsByDepot(@PathVariable Long depotId) {
        return ResponseEntity.ok(storedItemService.getItemsByDepot(depotId));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<StoredItemDTO>> getExpiringItems(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        return ResponseEntity.ok(storedItemService.getExpiringItems(beforeDate));
    }

    @GetMapping("/depot/{depotId}/expiring")
    public ResponseEntity<List<StoredItemDTO>> getExpiringItemsByDepot(
            @PathVariable Long depotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        return ResponseEntity.ok(storedItemService.getExpiringItemsByDepot(depotId, beforeDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoredItemDTO> updateStoredItem(
            @PathVariable Long id,
            @RequestBody StoredItemDTO storedItemDTO) {
        StoredItemDTO updatedItem = storedItemService.updateStoredItem(id, storedItemDTO);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<StoredItemDTO> adjustQuantity(
            @PathVariable Long id,
            @RequestParam Double quantityChange) {
        StoredItemDTO updatedItem = storedItemService.adjustQuantity(id, quantityChange);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStoredItem(@PathVariable Long id) {
        storedItemService.deleteStoredItem(id);
        return ResponseEntity.noContent().build();
    }
}