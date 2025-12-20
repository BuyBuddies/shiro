package com.buybuddies.shiro.data.grocery_item;

import com.buybuddies.shiro.data.BaseDTO;
import com.buybuddies.shiro.data.enums.ItemCategory;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class GroceryItemDTO extends BaseDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @NotNull(message = "Category is required")
    private ItemCategory category;

    @NotNull(message = "unit is required")
    private MeasurementUnit unit;

    private String imageUrl;
}