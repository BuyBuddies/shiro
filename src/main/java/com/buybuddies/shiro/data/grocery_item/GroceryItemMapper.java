package com.buybuddies.shiro.data.grocery_item;

import org.springframework.stereotype.Component;

@Component
public class GroceryItemMapper {

    public GroceryItem toEntity(GroceryItemDTO dto) {
        return GroceryItem.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .barcode(dto.getBarcode())
                .category(dto.getCategory())
                .unit(dto.getUnit())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    public GroceryItemDTO toResponseDTO(GroceryItem entity) {
        GroceryItemDTO dto = GroceryItemDTO.builder()
//                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .barcode(entity.getBarcode())
                .category(entity.getCategory())
                .unit(entity.getUnit())
                .imageUrl(entity.getImageUrl())
                .build();

        // Set BaseDTO fields
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public void updateEntityFromDTO(GroceryItemDTO dto, GroceryItem entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setBarcode(dto.getBarcode());
        entity.setCategory(dto.getCategory());
        entity.setUnit(dto.getUnit());
        entity.setImageUrl(dto.getImageUrl());
    }
}
