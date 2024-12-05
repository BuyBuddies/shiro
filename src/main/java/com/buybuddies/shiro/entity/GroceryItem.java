package com.buybuddies.shiro.entity;


import com.buybuddies.shiro.enums.ItemCategory;
import com.buybuddies.shiro.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "grocery_items")
@Data
public class GroceryItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true)
    private String barcode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeasurementUnit defaultUnit;

//    @OneToMany(mappedBy = "groceryItem")
//    private Set<StoredItem> storedItems = new HashSet<>();
//
//    @OneToMany(mappedBy = "groceryItem")
//    private Set<GroceryListItem> groceryListItems = new HashSet<>();
}
