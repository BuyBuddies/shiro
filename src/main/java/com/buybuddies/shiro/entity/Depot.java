package com.buybuddies.shiro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "depots")
@Data
public class Depot extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @OneToMany(mappedBy = "depot",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<StoredItem> storedItems = new HashSet<>();
}
