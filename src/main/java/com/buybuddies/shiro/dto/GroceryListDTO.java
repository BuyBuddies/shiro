package com.buybuddies.shiro.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class GroceryListDTO {
    private Long id;
    private String name;
    private String description;
    private String ownerId;
    private Long homeId;
    private String status;
    private Set<String> memberIds = new HashSet<>();
}