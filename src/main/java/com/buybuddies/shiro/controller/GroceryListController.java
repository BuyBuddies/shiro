package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.GroceryListDTO;
import com.buybuddies.shiro.service.GroceryListService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-lists")
@RequiredArgsConstructor
@Slf4j
public class GroceryListController {
    private final GroceryListService groceryListService;

    @PostMapping
    public ResponseEntity<GroceryListDTO> createGroceryList(
            HttpServletRequest request,
            @RequestBody GroceryListDTO groceryListDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Creating grocery list: {}", groceryListDTO.getName());

        groceryListDTO.setOwnerId(firebaseUid);

        return new ResponseEntity<>(
                groceryListService.createGroceryList(groceryListDTO),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{listId}")
    public ResponseEntity<GroceryListDTO> getGroceryList(
            HttpServletRequest request,
            @PathVariable Long listId
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Getting grocery list: {}", listId);

        GroceryListDTO list = groceryListService.getGroceryList(listId);

        if (!list.getOwnerId().equals(firebaseUid) &&
                !list.getMemberIds().contains(firebaseUid)) {
            log.warn("Access denied for user {} to list {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<GroceryListDTO>> getMyLists(
            HttpServletRequest request
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Getting all lists for user: {}", firebaseUid);

        return ResponseEntity.ok(groceryListService.getAllAccessibleLists(firebaseUid));
    }


    @PutMapping("/{listId}")
    public ResponseEntity<GroceryListDTO> updateGroceryList(
            HttpServletRequest request,
            @PathVariable Long listId,
            @RequestBody GroceryListDTO groceryListDTO
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Updating grocery list: {}", listId);

        GroceryListDTO existingList = groceryListService.getGroceryList(listId);

        if (!existingList.getOwnerId().equals(firebaseUid)) {
            log.warn("Update attempt by non-owner. User: {}, List: {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListService.updateGroceryList(listId, groceryListDTO));
    }

    @PostMapping("/{listId}/members/{memberFirebaseUid}")
    public ResponseEntity<GroceryListDTO> addMember(
            HttpServletRequest request,
            @PathVariable Long listId,
            @PathVariable String memberFirebaseUid
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Adding member {} to list {}", memberFirebaseUid, listId);

        GroceryListDTO list = groceryListService.getGroceryList(listId);
        if (!list.getOwnerId().equals(firebaseUid)) {
            log.warn("Add member attempt by non-owner. User: {}, List: {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListService.addMember(listId, memberFirebaseUid));
    }

    @DeleteMapping("/{listId}/members/{memberFirebaseUid}")
    public ResponseEntity<GroceryListDTO> removeMember(
            HttpServletRequest request,
            @PathVariable Long listId,
            @PathVariable String memberFirebaseUid
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Removing member {} from list {}", memberFirebaseUid, listId);

        GroceryListDTO list = groceryListService.getGroceryList(listId);
        if (!list.getOwnerId().equals(firebaseUid)) {
            log.warn("Remove member attempt by non-owner. User: {}, List: {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(groceryListService.removeMember(listId, memberFirebaseUid));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteGroceryList(
            HttpServletRequest request,
            @PathVariable Long listId
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Deleting grocery list: {}", listId);

        GroceryListDTO list = groceryListService.getGroceryList(listId);
        if (!list.getOwnerId().equals(firebaseUid)) {
            log.warn("Delete attempt by non-owner. User: {}, List: {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        groceryListService.deleteGroceryList(listId, firebaseUid);
        return ResponseEntity.noContent().build();
    }
}