package com.buybuddies.shiro.dto;

import com.buybuddies.shiro.enums.ItemCategory;
import com.buybuddies.shiro.enums.MeasurementUnit;
import lombok.Data;

@Data
public class GroceryItemDTO {
    Long id;
    String name;
    String description;
    String barcode;
    ItemCategory category;
    MeasurementUnit unit;
}