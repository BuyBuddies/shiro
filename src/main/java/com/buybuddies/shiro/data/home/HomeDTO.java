package com.buybuddies.shiro.data.home;

import com.buybuddies.shiro.data.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class HomeDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Set<Long> memberIds = new HashSet<>();
}