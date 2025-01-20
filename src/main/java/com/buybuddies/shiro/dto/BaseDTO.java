package com.buybuddies.shiro.dto;

import lombok.Data;

@Data
public abstract class BaseDTO {
    private Long updatedAt;
    private String createdAt;
}
