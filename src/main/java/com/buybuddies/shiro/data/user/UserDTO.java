package com.buybuddies.shiro.data.user;

import com.buybuddies.shiro.data.BaseDTO;
import lombok.*;

/**
 * DTO for returning user data to client
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {
    private Long id;
    private String email;
    private String name;
    private Role role;
}

