package com.buybuddies.shiro.data;

import lombok.Data;

import java.time.Instant;

/**
 * Base DTO with common audit fields
 * All DTOs that represent entities extending BaseEntity should extend this
 */
@Data
public abstract class BaseDTO {
    private Instant createdAt;
    private Instant updatedAt;
}