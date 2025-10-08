package com.buybuddies.shiro.data.grocery_item;


import com.buybuddies.shiro.data.BaseEntity;
import com.buybuddies.shiro.data.enums.ItemCategory;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grocery_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class GroceryItem extends BaseEntity {
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
}
