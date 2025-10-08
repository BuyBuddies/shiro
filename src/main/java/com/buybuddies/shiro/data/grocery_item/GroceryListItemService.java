package com.buybuddies.shiro.data.grocery_item;

import com.buybuddies.shiro.data.grocery_list_item.GroceryListItemDTO;
import com.buybuddies.shiro.data.grocery_list.GroceryList;
import com.buybuddies.shiro.data.grocery_list_item.GroceryListItem;
import com.buybuddies.shiro.data.enums.ItemCategory;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import com.buybuddies.shiro.data.enums.PurchaseStatus;
import com.buybuddies.shiro.data.grocery_list_item.GroceryListItemRepository;
import com.buybuddies.shiro.data.grocery_list.GroceryListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroceryListItemService {
    private final GroceryListItemRepository groceryListItemRepository;
    private final GroceryListRepository groceryListRepository;
    private final GroceryItemRepository groceryItemRepository;


    @Transactional
    public GroceryListItemDTO createListItem(GroceryListItemDTO dto) {
        GroceryList groceryList = groceryListRepository.findById(dto.getGroceryListId())
                .orElseThrow(() -> new RuntimeException("Grocery list not found"));

        GroceryItem groceryItem = groceryItemRepository.findByNameIgnoreCase(dto.getGroceryItemName())
                .orElseGet(() -> {
                    GroceryItem newItem = new GroceryItem();
                    newItem.setName(dto.getGroceryItemName());
                    newItem.setCategory(ItemCategory.OTHER);
                    newItem.setDefaultUnit(dto.getUnit() != null ? dto.getUnit() : MeasurementUnit.PIECE);
                    return groceryItemRepository.save(newItem);
                });

        GroceryListItem item = new GroceryListItem();
        item.setGroceryList(groceryList);
        item.setGroceryItem(groceryItem);
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit() != null ? dto.getUnit() : groceryItem.getDefaultUnit());
        item.setStatus(dto.getStatus() != null ? dto.getStatus() : PurchaseStatus.PENDING);

        item = groceryListItemRepository.save(item);
        return convertToDTO(item);
    }

    public GroceryListItemDTO getListItem(Long id) {
        GroceryListItem item = groceryListItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list item not found"));
        return convertToDTO(item);
    }

    public List<GroceryListItemDTO> getItemsByList(Long listId) {
        return groceryListItemRepository.findByGroceryListId(listId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GroceryListItemDTO> getGroceryListItemsByUser(String userId) {
        return groceryListItemRepository.findByOwnerIdOrMemberId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GroceryListItemDTO> getItemsByListAndStatus(Long listId, PurchaseStatus status) {
        return groceryListItemRepository.findByGroceryListIdAndStatus(listId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroceryListItemDTO updateListItem(Long id, GroceryListItemDTO dto) {
        GroceryListItem item = groceryListItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list item not found"));

        if (dto.getGroceryItemName() != null && !dto.getGroceryItemName().equals(item.getGroceryItem().getName())) {
            GroceryItem groceryItem = groceryItemRepository.findByNameIgnoreCase(dto.getGroceryItemName())
                    .orElseThrow(() -> new RuntimeException("Grocery item not found"));
            item.setGroceryItem(groceryItem);
        }

        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setStatus(dto.getStatus());

        item = groceryListItemRepository.save(item);
        return convertToDTO(item);
    }

    @Transactional
    public GroceryListItemDTO updateItemStatus(Long id, PurchaseStatus status) {
        GroceryListItem item = groceryListItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery list item not found"));

        item.setStatus(status);
        item = groceryListItemRepository.save(item);
        return convertToDTO(item);
    }

    @Transactional
    public void deleteListItem(Long id) {
        if (!groceryListItemRepository.existsById(id)) {
            throw new RuntimeException("Grocery list item not found");
        }
        groceryListItemRepository.deleteById(id);
    }

    private GroceryListItemDTO convertToDTO(GroceryListItem item) {
        GroceryListItemDTO dto = new GroceryListItemDTO();
        dto.setId(item.getId());
        dto.setGroceryListId(item.getGroceryList().getId());
        dto.setGroceryItemName(item.getGroceryItem().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setStatus(item.getStatus());
        return dto;
    }
}