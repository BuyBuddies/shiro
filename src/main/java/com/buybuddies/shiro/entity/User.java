package com.buybuddies.shiro.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firebaseUid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;
}

