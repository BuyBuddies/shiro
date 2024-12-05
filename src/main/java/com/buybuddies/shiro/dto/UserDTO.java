package com.buybuddies.shiro.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String firebaseUid;
    private String email;
    private String name;
}