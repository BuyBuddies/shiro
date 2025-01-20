package com.buybuddies.shiro.service;

import com.buybuddies.shiro.dto.GroceryListDTO;
import com.buybuddies.shiro.entity.GroceryList;
import com.buybuddies.shiro.entity.GroceryListItem;
import com.buybuddies.shiro.entity.User;
import com.buybuddies.shiro.exception.ResourceNotFoundException;
import com.buybuddies.shiro.repository.GroceryListRepository;
import com.buybuddies.shiro.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroceryListService {
    private final GroceryListRepository groceryListRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public GroceryListDTO createGroceryList(GroceryListDTO groceryListDTO) {
        log.info("Creating new grocery list. Name: '{}', Owner Firebase UID: '{}'",
                groceryListDTO.getName(), groceryListDTO.getOwnerId());

        User owner = userRepository.findByFirebaseUid(groceryListDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        GroceryList groceryList = new GroceryList();
        groceryList.setName(groceryListDTO.getName());
        groceryList.setDescription(groceryListDTO.getDescription());
        groceryList.setOwner(owner);
        groceryList.setStatus("ACTIVE");

        groceryList = groceryListRepository.save(groceryList);
        log.info("Successfully created grocery list with ID: {}", groceryList.getId());

        return convertToDTO(groceryList);
    }

    public GroceryListDTO getGroceryList(Long id) {
        log.debug("Fetching grocery list with ID: {}", id);
        GroceryList groceryList = groceryListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));
        return convertToDTO(groceryList);
    }

    public List<GroceryListDTO> getAllAccessibleLists(String firebaseUid) {
        log.debug("Fetching all accessible lists for user: {}", firebaseUid);
        return groceryListRepository.findByOwnerOrMemberFirebaseUid(firebaseUid).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroceryListDTO updateGroceryList(Long id, GroceryListDTO dto) {
        log.info("Updating grocery list. ID: {}", id);
        GroceryList groceryList = groceryListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        groceryList.setName(dto.getName());
        groceryList.setDescription(dto.getDescription());

        if (dto.getStatus() != null) {
            groceryList.setStatus(dto.getStatus());
        }

        groceryList = groceryListRepository.save(groceryList);
        log.info("Successfully updated grocery list with ID: {}", id);
        return convertToDTO(groceryList);
    }


    @Transactional
    public GroceryListDTO addMember(Long listId, String memberFirebaseUid) {
        log.info("Adding member {} to list {}", memberFirebaseUid, listId);

        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        User member = userRepository.findByFirebaseUid(memberFirebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!groceryList.getMembers().contains(member)) {
            groceryList.getMembers().add(member);
            groceryList = groceryListRepository.save(groceryList);
            log.info("Successfully added member to list {}", listId);
        } else {
            log.debug("Member already in list {}", listId);
        }

        return convertToDTO(groceryList);
    }

    @Transactional
    public GroceryListDTO removeMember(Long listId, String memberFirebaseUid) {
        log.info("Removing member {} from list {}", memberFirebaseUid, listId);

        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        User member = userRepository.findByFirebaseUid(memberFirebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (groceryList.getMembers().remove(member)) {
            groceryList = groceryListRepository.save(groceryList);
            log.info("Successfully removed member from list {}", listId);
        } else {
            log.debug("Member was not in list {}", listId);
        }

        return convertToDTO(groceryList);
    }

    @Transactional
    public void deleteGroceryList(Long id, String ownerFirebaseUid) {
        log.info("Deleting grocery list {}. Owner: {}", id, ownerFirebaseUid);

        GroceryList groceryList = groceryListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        if (!groceryList.getOwner().getFirebaseUid().equals(ownerFirebaseUid)) {
            throw new RuntimeException("Not authorized to delete this list");
        }

        entityManager.createQuery("DELETE FROM GroceryListItem gli WHERE gli.groceryList.id = :listId")
                .setParameter("listId", id)
                .executeUpdate();

        groceryList.getMembers().clear();

        groceryListRepository.delete(groceryList);

        log.info("Successfully deleted grocery list {}", id);
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
                .map(User::getFirebaseUid)
                .collect(Collectors.toSet()));

        log.debug("Converted grocery list to DTO. Members count: {}", dto.getMemberIds().size());
        return dto;
    }

    public GroceryListDTO findByOwnerIdAndName(String ownerFirebaseUid, String name) {
        GroceryList list = groceryListRepository.findByOwnerFirebaseUidAndName(ownerFirebaseUid, name)
                .orElse(null);
        return list != null ? convertToDTO(list) : null;
    }

    public List<String> getListMemberEmails(Long listId) {
        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        return groceryList.getMembers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroceryListDTO addMemberByEmail(Long listId, String memberEmail) {
        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found with id: " + listId));

        User memberUser = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + memberEmail));

        // Check if user is already a member
        if (groceryList.getMembers().stream().anyMatch(member -> member.getId().equals(memberUser.getId()))) {
            throw new DuplicateKeyException("User is already a member of this list");
        }

        groceryList.getMembers().add(memberUser);
        GroceryList savedList = groceryListRepository.save(groceryList);

        return convertToDTO(savedList);
    }

    @Transactional
    public GroceryListDTO removeMemberByEmail(Long listId, String email) {
        GroceryList groceryList = groceryListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found with id: " + listId));

        User memberToRemove = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Remove the member
        groceryList.getMembers().remove(memberToRemove);

        // Save the updated list
        GroceryList savedList = groceryListRepository.save(groceryList);

        return convertToDTO(savedList);
    }
}