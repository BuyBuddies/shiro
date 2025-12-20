package com.buybuddies.shiro.data.enums;


import lombok.Getter;

@Getter
public enum MeasurementUnit {
    PIECE("piece", "pcs"),
    KILOGRAM("kilogram", "kg"),
    GRAM("gram", "g"),
    LITER("liter", "L"),
    MILLILITER("milliliter", "mL"),
    CUP("cup", "cup"),
    TABLESPOON("tablespoon", "tbsp"),
    TEASPOON("teaspoon", "tsp"),
    POUND("pound", "lb"),
    OUNCE("ounce", "oz"),
    PACKAGE("package", "pkg"),
    BOTTLE("bottle", "btl"),
    CAN("can", "can"),
    BOX("box", "box"),
    BAG("bag", "bag");

    private final String displayName;
    private final String shortcut;

    MeasurementUnit(String displayName, String shortcut) {
        this.displayName = displayName;
        this.shortcut = shortcut;
    }

}