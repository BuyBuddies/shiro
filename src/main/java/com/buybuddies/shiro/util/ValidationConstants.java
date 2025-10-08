package com.buybuddies.shiro.util;

/**
 * Centralized validation patterns and messages for DTOs
 * Use these constants in @Pattern annotations for consistency
 */
public interface ValidationConstants {

    // Regex patterns
    String ALPHANUMERIC_SPACE = "^[a-zA-Z0-9 ]+$";
    String ALPHANUMERIC_ONLY = "^[a-zA-Z0-9]+$";
    String LETTERS_ONLY = "^[a-zA-Z ]+$";
    String NUMBERS_ONLY = "^[0-9]+$";

    // Unicode support for international names
    String UNICODE_ALPHANUMERIC_SPACE = "^[\\p{L}\\p{N} ]+$";

    // Validation Messages
    String NAME_PATTERN_MSG = "Name can only contain letters, numbers, and spaces";
    String LETTERS_ONLY_MSG = "This field can only contain letters and spaces";
    String NUMBERS_ONLY_MSG = "This field can only contain numbers";

    // Size constraints
    int NAME_MIN_LENGTH = 2;
    int NAME_MAX_LENGTH = 30;
}