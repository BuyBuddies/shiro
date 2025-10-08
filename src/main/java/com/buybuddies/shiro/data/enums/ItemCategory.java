package com.buybuddies.shiro.data.enums;

import lombok.Getter;

@Getter
public enum ItemCategory{
    PRODUCE("Produce"),
    DAIRY("Dairy"),
    MEAT("Meat & Seafood"),
    PANTRY("Pantry"),
    FROZEN("Frozen"),
    BEVERAGES("Beverages"),
    SNACKS("Snacks"),
    HOUSEHOLD("Household"),
    BAKERY("Bakery"),
    PERSONAL_CARE("Personal Care"),
    OTHER("Other");

    private final String displayName;

    ItemCategory(String displayName) {
        this.displayName = displayName;
    }

}
