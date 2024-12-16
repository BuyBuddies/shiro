package com.buybuddies.shiro.service;

import com.buybuddies.shiro.dto.GroceryListDTO;
import com.buybuddies.shiro.entity.GroceryList;
import com.buybuddies.shiro.entity.User;
import com.buybuddies.shiro.repository.GroceryListRepository;
import com.buybuddies.shiro.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroceryListService {
    private final GroceryListRepository groceryListRepository;
    private final UserRepository userRepository;


    public GroceryListDTO createGroceryList(GroceryListDTO groceryListDTO) {
        log.info("Creating new grocery list. Name: '{}', Owner ID: '{}'",
                groceryListDTO.getName(), groceryListDTO.getOwnerId());

        User owner = userRepository.findByFirebaseUid(groceryListDTO.getOwnerId())
                .orElseThrow(() -> {
                    log.error("Failed to create grocery list. Owner not found with Firebase UID: {}",
                            groceryListDTO.getOwnerId());
                    return new RuntimeException("Owner not found");
                });

        GroceryList groceryList = new GroceryList();
        groceryList.setName(groceryListDTO.getName());
        groceryList.setDescription(groceryListDTO.getDescription());
        groceryList.setOwner(owner);

        if (groceryListDTO.getMemberIds() != null && !groceryListDTO.getMemberIds().isEmpty()) {
            log.debug("Adding {} members to grocery list", groceryListDTO.getMemberIds().size());
            Set<User> members = userRepository.findAllById(groceryListDTO.getMemberIds())
                    .stream()
                    .collect(Collectors.toSet());
            groceryList.setMembers(members);
        }

        groceryList = groceryListRepository.save(groceryList);
        log.info("Successfully created grocery list with ID: {}", groceryList.getId());

        return convertToDTO(groceryList);
    }

    public GroceryListDTO getGroceryList(Long id) {
        GroceryList groceryList = groceryListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));
        return convertToDTO(groceryList);
    }

    public List<GroceryListDTO> getGroceryListsByUser(Long userId) {
        return groceryListRepository.findByOwnerIdOrMembersId(userId, userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroceryListDTO updateGroceryList(Long id, GroceryListDTO dto) {
        GroceryList groceryList = groceryListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        groceryList.setName(dto.getName());
        groceryList.setDescription(dto.getDescription());

        if (dto.getOwnerId() != null && !dto.getOwnerId().equals(groceryList.getOwner().getId())) {
            User newOwner = userRepository.findByFirebaseUid(dto.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("New owner not found"));
            groceryList.setOwner(newOwner);
        }

        if (dto.getMemberIds() != null) {
            Set<User> members = userRepository.findAllById(dto.getMemberIds())
                    .stream()
                    .collect(Collectors.toSet());
            groceryList.setMembers(members);
        }

        groceryList = groceryListRepository.save(groceryList);
        return convertToDTO(groceryList);
    }

    @Transactional
    public GroceryListDTO addMember(Long listId, Long userId) {
        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        groceryList.getMembers().add(user);
        groceryList = groceryListRepository.save(groceryList);
        return convertToDTO(groceryList);
    }

    @Transactional
    public GroceryListDTO removeMember(Long listId, Long userId) {
        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        groceryList.getMembers().remove(user);
        groceryList = groceryListRepository.save(groceryList);
        return convertToDTO(groceryList);
    }

    @Transactional
    public void deleteGroceryList(Long id) {
        if (!groceryListRepository.existsById(id)) {
            throw new RuntimeException("Grocery list not found");
        }
        groceryListRepository.deleteById(id);
    }

    @Transactional
    public void deleteGroceryListByNameAndOwnerId(String name, String firebaseUid) {
        log.info("Attempting to delete grocery list. Name: '{}', Owner Firebase UID: '{}'",
                name, firebaseUid);

        // Find user by Firebase UID
        User owner = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> {
                    log.error("Failed to delete grocery list. Owner not found with Firebase UID: {}",
                            firebaseUid);
                    return new RuntimeException("User not found");
                });

        log.debug("Found owner in database. Internal ID: {}", owner.getId());

        // Find and delete the list
        GroceryList groceryList = groceryListRepository.findByNameAndOwner(name, owner)
                .orElseThrow(() -> {
                    log.error("Failed to delete grocery list. List not found with name '{}' and owner '{}'",
                            name, firebaseUid);
                    return new RuntimeException("Grocery list not found");
                });

        log.debug("Found grocery list to delete. ID: {}, Items count: {}, Members count: {}",
                groceryList.getId(),
                groceryList.getItems().size(),
                groceryList.getMembers().size());

        groceryListRepository.deleteByNameAndOwner(name, owner);
        log.info("Successfully deleted grocery list. Name: '{}', Owner Firebase UID: '{}'",
                name, firebaseUid);
    }

    private GroceryListDTO convertToDTO(GroceryList groceryList) {
        log.debug("Converting grocery list to DTO. List ID: {}", groceryList.getId());

        GroceryListDTO dto = new GroceryListDTO();
        dto.setId(groceryList.getId());
        dto.setName(groceryList.getName());
        dto.setDescription(groceryList.getDescription());
        dto.setOwnerId(groceryList.getOwner().getFirebaseUid());  // Using Firebase UID
        dto.setStatus(groceryList.getStatus());

        dto.setMemberIds(groceryList.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));

        log.debug("Converted grocery list to DTO. Members count: {}", dto.getMemberIds().size());
        return dto;
    }
}