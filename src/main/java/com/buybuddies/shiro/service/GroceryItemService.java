package com.buybuddies.shiro.service;

import com.buybuddies.shiro.dto.GroceryItemDTO;
import com.buybuddies.shiro.entity.GroceryItem;
import com.buybuddies.shiro.enums.ItemCategory;
import com.buybuddies.shiro.exception.ResourceNotFoundException;
import com.buybuddies.shiro.repository.GroceryItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroceryItemService {
    private final GroceryItemRepository groceryItemRepository;

    public GroceryItemDTO createGroceryItem(GroceryItemDTO groceryItemDTO) {
        if (groceryItemDTO.getBarcode() != null &&
                groceryItemRepository.existsByBarcode(groceryItemDTO.getBarcode())) {
            throw new RuntimeException("Item with this barcode already exists");
        }

        GroceryItem item = new GroceryItem();
        item.setName(groceryItemDTO.getName());
        item.setDescription(groceryItemDTO.getDescription());
        item.setBarcode(groceryItemDTO.getBarcode());
        item.setCategory(groceryItemDTO.getCategory());
        item.setDefaultUnit(groceryItemDTO.getDefaultUnit());

        item = groceryItemRepository.save(item);
        return convertToDTO(item);
    }

    public GroceryItemDTO getGroceryItem(Long id) {
        GroceryItem item = groceryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery item not found"));
        return convertToDTO(item);
    }

    public GroceryItemDTO getGroceryItemByBarcode(String barcode) {
        GroceryItem item = groceryItemRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Grocery item not found"));
        return convertToDTO(item);
    }

    public List<GroceryItemDTO> getAllGroceryItems() {
        return groceryItemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GroceryItemDTO> getGroceryItemsByCategory(ItemCategory category) {
        return groceryItemRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GroceryItemDTO> searchGroceryItems(String name) {
        return groceryItemRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroceryItemDTO updateGroceryItem(Long id, GroceryItemDTO dto) {
        GroceryItem item = groceryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grocery item not found"));

        if (dto.getBarcode() != null && !dto.getBarcode().equals(item.getBarcode()) &&
                groceryItemRepository.existsByBarcode(dto.getBarcode())) {
            throw new RuntimeException("Item with this barcode already exists");
        }

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setBarcode(dto.getBarcode());
        item.setCategory(dto.getCategory());
        item.setDefaultUnit(dto.getDefaultUnit());

        item = groceryItemRepository.save(item);
        return convertToDTO(item);
    }

    @Transactional
    public void deleteGroceryItem(Long id) {
        if (!groceryItemRepository.existsById(id)) {
            throw new RuntimeException("Grocery item not found");
        }
        groceryItemRepository.deleteById(id);
    }

    private GroceryItemDTO convertToDTO(GroceryItem item) {
        GroceryItemDTO dto = new GroceryItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setBarcode(item.getBarcode());
        dto.setCategory(item.getCategory());
        dto.setDefaultUnit(item.getDefaultUnit());
        return dto;
    }
}