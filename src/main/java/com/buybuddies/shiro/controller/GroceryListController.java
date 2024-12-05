package com.buybuddies.shiro.controller;

import com.buybuddies.shiro.dto.GroceryListDTO;
import com.buybuddies.shiro.entity.GroceryList;
import com.buybuddies.shiro.service.GroceryListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-lists")
@RequiredArgsConstructor
public class GroceryListController {
    private final GroceryListService groceryListService;

    @PostMapping
    public ResponseEntity<GroceryListDTO> createGroceryList(@RequestBody GroceryListDTO groceryListDTO) {
        return new ResponseEntity<>(groceryListService.createGroceryList(groceryListDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroceryListDTO> getGroceryList(@PathVariable Long id) {
        return ResponseEntity.ok(groceryListService.getGroceryList(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroceryListDTO>> getGroceryListsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(groceryListService.getGroceryListsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroceryListDTO> updateGroceryList(
            @PathVariable Long id,
            @RequestBody GroceryListDTO groceryListDTO) {
        return ResponseEntity.ok(groceryListService.updateGroceryList(id, groceryListDTO));
    }

    @PostMapping("/{listId}/members/{userId}")
    public ResponseEntity<GroceryListDTO> addMember(
            @PathVariable Long listId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(groceryListService.addMember(listId, userId));
    }

    @DeleteMapping("/{listId}/members/{userId}")
    public ResponseEntity<GroceryListDTO> removeMember(
            @PathVariable Long listId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(groceryListService.removeMember(listId, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroceryList(@PathVariable Long id) {
        groceryListService.deleteGroceryList(id);
        return ResponseEntity.noContent().build();
    }
}