package com.buybuddies.shiro.dto;

import lombok.Data;

@Data
public class DepotDTO extends BaseDTO{
    private Long id;
    private String name;
    private String description;
    private Long homeId;
}