package com.buybuddies.shiro.service;

import com.buybuddies.shiro.dto.StoredItemDTO;
import com.buybuddies.shiro.entity.Depot;
import com.buybuddies.shiro.entity.GroceryItem;
import com.buybuddies.shiro.entity.StoredItem;
import com.buybuddies.shiro.enums.ItemCategory;
import com.buybuddies.shiro.enums.MeasurementUnit;
import com.buybuddies.shiro.repository.DepotRepository;
import com.buybuddies.shiro.repository.GroceryItemRepository;
import com.buybuddies.shiro.repository.StoredItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoredItemService {
    private final StoredItemRepository storedItemRepository;
    private final DepotRepository depotRepository;
    private final GroceryItemRepository groceryItemRepository;

    @Transactional
    public StoredItemDTO createStoredItem(StoredItemDTO dto) {
        Depot depot = depotRepository.findById(dto.getDepotId())
                .orElseThrow(() -> new RuntimeException("Depot not found"));

        GroceryItem groceryItem = groceryItemRepository.findById(dto.getGroceryItemId())
                .orElseGet(() -> {
                    GroceryItem newItem = new GroceryItem();
                    newItem.setName(dto.getGroceryItemName());
                    newItem.setCategory(ItemCategory.OTHER);
                    newItem.setDefaultUnit(dto.getUnitEnum() != null ?
                            dto.getUnitEnum() : MeasurementUnit.PIECE);
                    return groceryItemRepository.save(newItem);
                });

        List<StoredItem> existingItems = storedItemRepository
                .findByDepotIdAndGroceryItemId(depot.getId(), groceryItem.getId());

        if (!existingItems.isEmpty()) {
            StoredItem existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            existingItem.setExpirationDate(dto.getExpirationDate());
            return convertToDTO(storedItemRepository.save(existingItem));
        }

        StoredItem storedItem = new StoredItem();
        storedItem.setGroceryItem(groceryItem);
        storedItem.setDepot(depot);
        storedItem.setQuantity(dto.getQuantity());
        storedItem.setUnit(dto.getUnitEnum() != null ?
                dto.getUnitEnum() : groceryItem.getDefaultUnit());
        storedItem.setExpirationDate(dto.getExpirationDate());

        storedItem = storedItemRepository.save(storedItem);
        return convertToDTO(storedItem);
    }

    public StoredItemDTO getStoredItem(Long id) {
        StoredItem item = storedItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stored item not found"));
        return convertToDTO(item);
    }

    public List<StoredItemDTO> getItemsByDepot(Long depotId) {
        return storedItemRepository.findByDepotId(depotId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StoredItemDTO> getExpiringItems(LocalDateTime beforeDate) {
        return storedItemRepository.findByExpirationDateBefore(beforeDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StoredItemDTO> getExpiringItemsByDepot(Long depotId, LocalDateTime beforeDate) {
        return storedItemRepository.findByDepotIdAndExpirationDateBefore(depotId, beforeDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoredItemDTO updateStoredItem(Long id, StoredItemDTO dto) {
        StoredItem item = storedItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stored item not found"));

        if (dto.getQuantity() <= 0) {
            storedItemRepository.delete(item);
            return null;
        }

        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnitEnum());
        item.setExpirationDate(dto.getExpirationDate());

        item = storedItemRepository.save(item);
        return convertToDTO(item);
    }

    @Transactional
    public StoredItemDTO adjustQuantity(Long id, Double quantityChange) {
        StoredItem item = storedItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stored item not found"));

        Double newQuantity = item.getQuantity() + quantityChange;
        if (newQuantity <= 0) {
            storedItemRepository.delete(item);
            return null;
        }

        item.setQuantity(newQuantity);
        item = storedItemRepository.save(item);
        return convertToDTO(item);
    }

    @Transactional
    public void deleteStoredItem(Long id) {
        if (!storedItemRepository.existsById(id)) {
            throw new RuntimeException("Stored item not found");
        }
        storedItemRepository.deleteById(id);
    }

    private StoredItemDTO convertToDTO(StoredItem item) {
        StoredItemDTO dto = new StoredItemDTO();
        dto.setId(item.getId());
        dto.setGroceryItemId(item.getGroceryItem().getId());
        dto.setGroceryItemName(item.getGroceryItem().getName());
        dto.setDepotId(item.getDepot().getId());
        dto.setDepotName(item.getDepot().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitFromEnum(item.getUnit());
        dto.setExpirationDate(item.getExpirationDate());
        return dto;
    }
}