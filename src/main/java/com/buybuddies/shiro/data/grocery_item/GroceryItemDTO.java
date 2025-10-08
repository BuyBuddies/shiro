package com.buybuddies.shiro.data.grocery_item;

import com.buybuddies.shiro.data.BaseDTO;
import com.buybuddies.shiro.data.enums.ItemCategory;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroceryItemDTO extends BaseDTO {
    Long id;
    String name;
    String description;
    String barcode;
    ItemCategory category;
    MeasurementUnit unit;
}