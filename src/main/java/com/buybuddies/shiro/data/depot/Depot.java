package com.buybuddies.shiro.data.depot;

import com.buybuddies.shiro.data.home.Home;
import com.buybuddies.shiro.data.BaseEntity;
import com.buybuddies.shiro.data.depot_item.StoredItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "depots")
@Data
@EqualsAndHashCode(callSuper = true)
public class Depot extends BaseEntity {
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
