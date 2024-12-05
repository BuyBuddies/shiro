package com.buybuddies.shiro.entity;

import com.buybuddies.shiro.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stored_items")
@Data
public class StoredItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grocery_item_id", nullable = false)
    private GroceryItem groceryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depot_id", nullable = false)
    private Depot depot;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeasurementUnit unit;

    private LocalDateTime expirationDate;


}
