package com.buybuddies.shiro.dto;

import lombok.Data;

@Data
public class DepotDTO {
    private Long id;
    private String name;
    private String description;
    private Long homeId;
}