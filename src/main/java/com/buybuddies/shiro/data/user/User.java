package com.buybuddies.shiro.data.user;


import com.buybuddies.shiro.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firebaseUid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated
    @Column(nullable = false)
    private Role role;
}

