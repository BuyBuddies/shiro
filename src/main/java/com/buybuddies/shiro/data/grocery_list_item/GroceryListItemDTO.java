package com.buybuddies.shiro.data.grocery_list_item;

import com.buybuddies.shiro.data.BaseDTO;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import com.buybuddies.shiro.data.enums.PurchaseStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroceryListItemDTO extends BaseDTO {
    private Long id;
    private Long groceryListId;
//    private Long groceryItemId;
    private String groceryItemName;
    private Double quantity;
    private MeasurementUnit unit;
    private PurchaseStatus status;
}