package com.buybuddies.shiro.data.home;


import com.buybuddies.shiro.data.user.User;
import com.buybuddies.shiro.data.grocery_list.GroceryList;
import com.buybuddies.shiro.data.BaseEntity;
import com.buybuddies.shiro.data.depot.Depot;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "homes")
@Data
@EqualsAndHashCode(callSuper = true)
public class Home extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "home_members",
            joinColumns = @JoinColumn(name = "home_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroceryList> groceryLists = new HashSet<>();

    @OneToMany(
            mappedBy = "home",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Depot> depots = new HashSet<>();
}