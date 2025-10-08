package com.buybuddies.shiro.data.depot_item;

import com.buybuddies.shiro.data.BaseDTO;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoredItemDTO extends BaseDTO {
    private Long id;
//    private Long groceryItemId;
    private String groceryItemName;
    private Long depotId;
    private String depotName;
    private Double quantity;
    private String unit;
    private LocalDateTime expirationDate;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public MeasurementUnit getUnitEnum() {
        try {
            return unit != null ? MeasurementUnit.valueOf(unit.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid measurement unit: " + unit);
        }
    }

    public void setUnitFromEnum(MeasurementUnit measurementUnit) {
        this.unit = measurementUnit != null ? measurementUnit.name() : null;
    }
}