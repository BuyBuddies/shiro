package com.buybuddies.shiro.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class GroceryListDTO {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Set<Long> memberIds = new HashSet<>();
//    private Set<String> memberNames = new HashSet<>();
}