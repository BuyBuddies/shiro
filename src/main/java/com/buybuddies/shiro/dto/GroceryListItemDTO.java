package com.buybuddies.shiro.dto;

import com.buybuddies.shiro.enums.MeasurementUnit;
import com.buybuddies.shiro.enums.PurchaseStatus;
import lombok.Data;

@Data
public class GroceryListItemDTO {
    private Long id;
    private Long groceryListId;
//    private Long groceryItemId;
    private String groceryItemName;
    private Double quantity;
    private MeasurementUnit unit;
    private String notes;
    private PurchaseStatus status;
}