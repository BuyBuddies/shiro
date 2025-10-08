package com.buybuddies.shiro.data.grocery_list;

import com.buybuddies.shiro.data.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroceryListDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private String ownerId;
    private Long homeId;
    private String status;
    private Set<String> memberIds = new HashSet<>();
}