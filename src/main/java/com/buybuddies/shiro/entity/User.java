package com.buybuddies.shiro.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

//    @OneToMany(mappedBy = "owner")
//    private Set<Home> ownedHomes = new HashSet<>();
//
//    @ManyToMany(mappedBy = "members")
//    private Set<Home> sharedHomes = new HashSet<>();
//
//    @ManyToMany(mappedBy = "members")
//    private Set<GroceryList> sharedLists = new HashSet<>();
}

