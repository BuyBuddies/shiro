package com.buybuddies.shiro.data.grocery_list_item;

import com.buybuddies.shiro.data.grocery_list.GroceryListDTO;
import com.buybuddies.shiro.data.enums.PurchaseStatus;
import com.buybuddies.shiro.data.grocery_item.GroceryListItemService;
import com.buybuddies.shiro.data.grocery_list.GroceryListService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grocery_list_items")
@RequiredArgsConstructor
@Slf4j
public class GroceryListItemController {
    private final GroceryListItemService groceryListItemService;
    private final GroceryListService groceryListService;

    @PostMapping
    public ResponseEntity<GroceryListItemDTO> createListItem(
            HttpServletRequest request,
            @RequestBody GroceryListItemDTO listItemDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Creating grocery list item for list: {}", listItemDTO.getGroceryListId());

        // Check if user has access to the list
        GroceryListDTO list = groceryListService.getGroceryList(listItemDTO.getGroceryListId());
        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Create item attempt for unauthorized list. User: {}, List: {}",
                    firebaseUid, listItemDTO.getGroceryListId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return new ResponseEntity<>(
                groceryListItemService.createListItem(listItemDTO),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/user")
    public ResponseEntity<List<GroceryListItemDTO>> getAllUserItems(
            HttpServletRequest request
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Getting all grocery list items for user: {}", firebaseUid);

        List<GroceryListItemDTO> accessibleLists = groceryListItemService.getGroceryListItemsByUser(firebaseUid);

        List<GroceryListItemDTO> allItems = accessibleLists.stream()
                .flatMap(list -> groceryListItemService.getItemsByList(list.getId()).stream())
                .collect(Collectors.toList());

        allItems.forEach(item -> log.info("Sending grocery list item: {}", item));

        return ResponseEntity.ok(allItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroceryListItemDTO> getListItem(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Getting grocery list item: {}", id);

        GroceryListItemDTO item = groceryListItemService.getListItem(id);
        GroceryListDTO list = groceryListService.getGroceryList(item.getGroceryListId());

        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Access denied for user {} to item {}", firebaseUid, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(item);
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<GroceryListItemDTO>> getItemsByList(
            HttpServletRequest request,
            @PathVariable Long listId
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Getting items for list: {}", listId);

        GroceryListDTO list = groceryListService.getGroceryList(listId);
        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Access denied for user {} to list items {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListItemService.getItemsByList(listId));
    }

    @GetMapping("/list/{listId}/status/{status}")
    public ResponseEntity<List<GroceryListItemDTO>> getItemsByListAndStatus(
            HttpServletRequest request,
            @PathVariable Long listId,
            @PathVariable PurchaseStatus status
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Getting items for list: {} with status: {}", listId, status);

        GroceryListDTO list = groceryListService.getGroceryList(listId);
        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Access denied for user {} to list items {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListItemService.getItemsByListAndStatus(listId, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroceryListItemDTO> updateListItem(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody GroceryListItemDTO listItemDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Updating grocery list item: {}", id);

        GroceryListItemDTO existingItem = groceryListItemService.getListItem(id);
        GroceryListDTO list = groceryListService.getGroceryList(existingItem.getGroceryListId());

        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Update attempt by unauthorized user. User: {}, Item: {}", firebaseUid, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListItemService.updateListItem(id, listItemDTO));
    }

    @PostMapping("/create_or_update")
    public ResponseEntity<GroceryListItemDTO> createOrUpdateGroceryListItem(
            HttpServletRequest request,
            @RequestBody GroceryListItemDTO listItemDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Received create or update request for grocery list item in listId: {}", listItemDTO.getGroceryListId());

        try {
            // Validate the grocery list exists and the user has access
            GroceryListDTO list = groceryListService.getGroceryList(listItemDTO.getGroceryListId());
            if (!list.getOwnerId().equals(firebaseUid) && !list.getMemberIds().contains(firebaseUid)) {
                log.warn("Unauthorized access by user: {} for listId: {}", firebaseUid, listItemDTO.getGroceryListId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Check if the grocery list item exists
            GroceryListItemDTO existingItem = null;
            try {
                existingItem = groceryListItemService.getListItem(listItemDTO.getId());
            } catch (EntityNotFoundException e) {
                log.info("Grocery list item not found, creating a new one for listId: {}", listItemDTO.getGroceryListId());
            }

            // If the item exists, update it
            if (existingItem != null) {
                log.info("Updating grocery list item: {}", listItemDTO);
                return ResponseEntity.ok(groceryListItemService.updateListItem(existingItem.getId(), listItemDTO));
            }

            // If the item doesn't exist, create a new one
            log.info("Creating new grocery list item for listId: {}", listItemDTO.getGroceryListId());
            return new ResponseEntity<>(groceryListItemService.createListItem(listItemDTO), HttpStatus.CREATED);

        } catch (EntityNotFoundException e) {
            log.error("Grocery list not found for id: {}", listItemDTO.getGroceryListId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error processing grocery list item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<GroceryListItemDTO> updateItemStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody PurchaseStatus status
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Updating status for item: {} to: {}", id, status);

        GroceryListItemDTO existingItem = groceryListItemService.getListItem(id);
        GroceryListDTO list = groceryListService.getGroceryList(existingItem.getGroceryListId());

        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Status update attempt by unauthorized user. User: {}, Item: {}",
                    firebaseUid, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListItemService.updateItemStatus(id, status));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteListItem(
            HttpServletRequest request,
            @RequestBody GroceryListItemDTO itemDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Received delete request for grocery list item: {}", itemDTO.getId());

        GroceryListItemDTO existingItem = groceryListItemService.getListItem(itemDTO.getId());
        if (existingItem == null) {
            log.warn("Item not found for deletion: {}", itemDTO.getId());
            return ResponseEntity.notFound().build();
        }

        GroceryListDTO list = groceryListService.getGroceryList(existingItem.getGroceryListId());
        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Delete attempt by unauthorized user. User: {}, Item: {}",
                    firebaseUid, existingItem.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Deleting grocery list item with ID: {}", existingItem.getId());
        groceryListItemService.deleteListItem(existingItem.getId());
        log.info("Item deleted successfully");
        return ResponseEntity.noContent().build();
    }
}