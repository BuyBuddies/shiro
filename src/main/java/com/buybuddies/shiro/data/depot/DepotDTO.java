package com.buybuddies.shiro.data.depot;

import com.buybuddies.shiro.data.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DepotDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private Long homeId;
}