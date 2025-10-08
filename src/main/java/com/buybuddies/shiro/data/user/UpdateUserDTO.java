package com.buybuddies.shiro.data.user;

import com.buybuddies.shiro.util.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for updating user profile
 * Only allow updating safe fields
 */
@Data
public class UpdateUserDTO {

    @NotBlank(message = "Name cannot be blank")
    @Size(
            min = ValidationConstants.NAME_MIN_LENGTH,
            max = ValidationConstants.NAME_MAX_LENGTH,
            message = "Name must be between 2 and 100 characters"
    )
    @Pattern(
            regexp = ValidationConstants.ALPHANUMERIC_SPACE,
            message = ValidationConstants.NAME_PATTERN_MSG
    )
    private String name;
}