package com.buybuddies.shiro.authorization;

import com.buybuddies.shiro.data.BaseDTO;
import com.buybuddies.shiro.util.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for user registration request from Android app
 * Contains only the data client should send (firebaseUid - is retrieved from token)
 */
@Getter
@Setter
@ToString
public class RegisterRequestDTO extends BaseDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    public String email;

    @NotBlank(message = "Name is required")
    @Size(
            min = ValidationConstants.NAME_MIN_LENGTH,
            max = ValidationConstants.NAME_MAX_LENGTH
    )
    @Pattern(
            regexp = ValidationConstants.ALPHANUMERIC_SPACE,
            message = ValidationConstants.NAME_PATTERN_MSG
    )
    public String name;
}

