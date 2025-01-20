package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.GroceryListDTO;
import com.buybuddies.shiro.dto.UserDTO;
import com.buybuddies.shiro.entity.GroceryList;
import com.buybuddies.shiro.entity.User;
import com.buybuddies.shiro.exception.ResourceNotFoundException;
import com.buybuddies.shiro.service.GroceryListService;
import com.buybuddies.shiro.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grocery-lists")
@RequiredArgsConstructor
@Slf4j
public class GroceryListController {
    private final GroceryListService groceryListService;
    private final UserService userService;

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

        List<GroceryListDTO> allLists = groceryListService.getAllAccessibleLists(firebaseUid);

        allLists.forEach(list -> log.info("Sending grocery lists: {}", list));

        return ResponseEntity.ok(allLists);
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

    @PutMapping("/{listId}/name")
    public ResponseEntity<GroceryListDTO> updateListName(
            HttpServletRequest request,
            @PathVariable Long listId,
            @RequestBody String name
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Updating grocery list name: {} to: {}", listId, name);

        try {
            GroceryListDTO existingList = groceryListService.getGroceryList(listId);

            if (!existingList.getOwnerId().equals(firebaseUid)) {
                log.warn("Name update attempt by non-owner. User: {}, List: {}", firebaseUid, listId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            existingList.setName(name);
            return ResponseEntity.ok(groceryListService.updateGroceryList(listId, existingList));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/members/email")
    public ResponseEntity<GroceryListDTO> addMemberByEmail(
            HttpServletRequest request,
            @RequestBody GroceryListDTO listDto,
            @RequestParam String memberEmail
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Adding member with email {} to list {}", memberEmail, listDto.getName());

        // Verify the requester is either the owner or an existing member
        if (!firebaseUid.equals(listDto.getOwnerId())) {
            GroceryListDTO list = groceryListService.getGroceryList(listDto.getId());
            if (!list.getMemberIds().contains(firebaseUid)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            GroceryListDTO updatedList = groceryListService.addMemberByEmail(listDto.getId(), memberEmail);
            return ResponseEntity.ok(updatedList);
        } catch (ResourceNotFoundException e) {
            log.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{listId}/members")
    public ResponseEntity<List<String>> getListMembers(
            @PathVariable Long listId,
            HttpServletRequest request
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Fetching members for list {} by user {}", listId, firebaseUid);

        GroceryListDTO groceryList = groceryListService.getGroceryList(listId);

        // Check if user has access to the list
        if (!groceryList.getOwnerId().equals(firebaseUid) &&
                !groceryList.getMemberIds().contains(firebaseUid)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> memberEmails = groceryListService.getListMemberEmails(listId);
        return ResponseEntity.ok(memberEmails);
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

    @DeleteMapping("/{listId}/members")
    public ResponseEntity<GroceryListDTO> removeMemberByEmail(
            HttpServletRequest request,
            @PathVariable Long listId,
            @RequestParam String email
    ) {
        String firebaseUid = (String) request.getAttribute("firebaseUid");
        log.info("Removing member with email {} from list {}", email, listId);

        GroceryListDTO list = groceryListService.getGroceryList(listId);
        if (!list.getOwnerId().equals(firebaseUid)) {
            log.warn("Remove member attempt by non-owner. User: {}, List: {}", firebaseUid, listId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            GroceryListDTO updatedList = groceryListService.removeMemberByEmail(listId, email);
            return ResponseEntity.ok(updatedList);
        } catch (ResourceNotFoundException e) {
            log.warn("User not found with email: {}", email);
            return ResponseEntity.notFound().build();
        }
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